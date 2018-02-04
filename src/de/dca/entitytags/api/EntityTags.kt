package de.dca.entitytags.api

import de.dca.entitytags.exceptions.InternalException
import de.dca.entitytags.extensions.*
import de.dca.entitytags.util.DataWatcherUtil
import de.dca.entitytags.util.EntityIdRepository
import net.minecraft.server.v1_12_R1.*
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class EntityTags {

    companion object {

        private val VIEW_DISTANCE = 30
        private val viewDistanceSquared = VIEW_DISTANCE * VIEW_DISTANCE

        private val ENTITY_BYTE_ARMOR_STAND_MARKER: Byte = 0x10
        private val ENTITY_BYTE_ENTITY_INVISIBLE: Byte = 0x20

        private val ARMOR_STAND_ENTITY_CLASS = EntityArmorStand::class.java

        private val entityTagsMap : HashMap<LivingEntity, EntityTags> = HashMap()

        fun internalUpdateTag(tag: EntityTag) {
            entityTagsMap.values
                    .filter { it.entityTags.containsKey(tag) }
                    .forEach { it.update(tag) }
        }

        fun internalPlayerLeft(p: Player){
            entityTagsMap.values
                    .forEach { it.removePlayerFromAllTags(p) }
        }

        fun internalPlayerUpdate(p: Player){
            entityTagsMap.values
                    .forEach { it.checkPlayer(p) }
        }

        fun globalDispose(){
            entityTagsMap.forEach{ _, v -> v.dispose() }
            entityTagsMap.clear()
        }

        fun remove(tagsEntity: LivingEntity) : EntityTags? {
            val obj = entityTagsMap.remove(tagsEntity)
            obj?.dispose()
            return obj
        }

        @JvmStatic
        fun of(entity: LivingEntity) : EntityTags
                = entityTagsMap.computeIfAbsent(entity, ::EntityTags)

        @JvmStatic
        fun has(entity: LivingEntity) : Boolean
                = entityTagsMap.containsKey(entity)
    }

    private val _entity: LivingEntity

    val Entity: LivingEntity
        get() = _entity

    private var entityTags: LinkedHashMap<EntityTag, Int> = LinkedHashMap()
    private val entityTagPlayers: LinkedHashMap<EntityTag, MutableList<Player>> = LinkedHashMap()

    private val tmpPlayerMap: HashMap<Player, Int> = HashMap()
    private val tmpVector: Vector = Vector(0, 0, 0)

    val Size: Int
        get() = entityTags.size

    private val EntityAlive: Boolean
        get() = !_entity.isDead

    private constructor(entity: LivingEntity){
        this._entity = entity
    }

    fun add(tag: EntityTag) {
        if (!entityTags.containsKey(tag)) {
            tag.onAttach(this)
            entityTags.put(tag, EntityIdRepository.reserve())
            entityTagPlayers.put(tag, LinkedList())
            update()
        }
    }

    fun add(index: Int, tag: EntityTag) {
        if (!entityTags.containsKey(tag)) {
            tag.onAttach(this)
            entityTagPlayers.clear()
            var i = 0
            val iter = entityTags.keys.iterator()
            while (i < index && iter.hasNext()) {
                this.entityTagPlayers.put(iter.next(), LinkedList())
                i++
            }
            this.entityTagPlayers.put(tag, LinkedList())
            while (iter.hasNext()) {
                this.entityTagPlayers.put(iter.next(), LinkedList())
            }

            val updatedMap = LinkedHashMap<EntityTag, Int>()
            for (iterTag in this.entityTagPlayers.keys) {
                val value = entityTags[iterTag]
                if (value != null) {
                    updatedMap.put(iterTag, value)
                } else {
                    updatedMap.put(iterTag, EntityIdRepository.reserve())
                }
            }
            this.entityTags = updatedMap
            update()
        }
    }

    fun remove(tag: EntityTag): Boolean {
        if (entityTags.containsKey(tag)) {
            val entityId = entityTags.remove(tag) ?: return false
            val players = entityTagPlayers.remove(tag) ?: return false

            for (p in players) {
                p.PlayerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
            }

            EntityIdRepository.free(entityId)
            tag.onDetach(this)
            return true
        }
        return false
    }

    fun update(){
        if(!EntityAlive){
            this.dispose()
            return
        }
        for(p in _entity.world.players){
            checkPlayer(p)
        }
        for(tag in entityTagPlayers.keys) {
            updateMetadata(tag)
        }
    }

    fun update(tag: EntityTag){
        for (p in _entity.world.players) {
            checkPlayer(p)
        }
        updateMetadata(tag)
    }

    private fun updatePosition(){
        if(!EntityAlive){
            this.dispose()
            return
        }
        tmpPlayerMap.clear()

        for((tag, playerList) in entityTagPlayers){
            val entityId = getEntityTagId(tag)

            for(player in playerList){
                if(!tmpPlayerMap.containsKey(player))
                    tmpPlayerMap[player] = 0

                val tagIndex = tmpPlayerMap.put(player, tmpPlayerMap[player]!! + 1) ?: throw InternalException("HashMap error!")

                tmpVector.x = _entity.location.x
                tmpVector.y = calculateTagHeight(tagIndex)
                tmpVector.z = _entity.location.z

                val offset = tag.calculatePositionOffset(player, tmpVector)

                val packet = PacketPlayOutEntityTeleport()
                packet.setEntityId(entityId)
                packet.setOnGround(false)
                packet.setX(tmpVector.x + offset.x)
                packet.setY(tmpVector.y + offset.y)
                packet.setZ(tmpVector.z + offset.z)
                player.PlayerConnection.sendPacket(packet)
            }
        }
    }

    private fun updateMetadata(tag: EntityTag){
        if(!EntityAlive){
            this.dispose()
            return
        }
        val tagPlayers = entityTagPlayers[tag] ?: return
        for(p in tagPlayers){
            updateMetadata(tag, p)
        }
    }

    private fun updateMetadata(tag: EntityTag, p: Player) {
        val entityId = getEntityTagId(tag)
        val dataWatcher = generateDataWatcher(tag, p)

        p.PlayerConnection.sendPacket(PacketPlayOutEntityMetadata(entityId, dataWatcher, true))
    }

    private fun checkPlayer(player: Player) : Boolean {
        val inRange = player.world == _entity.world
            && player.location.distanceSquared(_entity.location) <= viewDistanceSquared

        var needPosUpdate = false

        if(inRange){
            for((tag, playerList) in entityTagPlayers){
                if(tag.isVisibleTo(player)){
                    if (!playerList.contains(player)){
                        playerList.add(player)
                        player.PlayerConnection.sendPacket(generateSpawnPacket(tag, player))
                        needPosUpdate = true
                    }
                }else{
                    if(playerList.remove(player)){
                        val entityId = getEntityTagId(tag)
                        player.PlayerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
                    }
                }
            }
        }else{
            removePlayerFromAllTags(player)
        }

        if(needPosUpdate)
            updatePosition()

        return inRange
    }

    fun dispose(){
        clear()
        EntityTags.remove(this._entity)
    }

    fun clear() {
        for ((entityTagObject, players) in this.entityTagPlayers) {
            val entityId = tryGetEntityTagId(entityTagObject) ?: continue

            for (p in players) {
                try {
                    p.PlayerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
                } catch (ex: Exception) {
                    ex.printStackTrace() // Ignore errors in dispose
                }

            }
            try {
                entityTagObject.onDetach(this)
            } catch (ex: Exception) {
                ex.printStackTrace() // Ignore errors in dispose
            }

            EntityIdRepository.free(entityId)
        }
        this.entityTags.clear()
        this.entityTagPlayers.clear()
        this.tmpPlayerMap.clear()
    }

    private fun tryGetEntityTagId(tag: EntityTag) : Int? {
        return entityTags[tag]
    }

    private fun getEntityTagId(tag: EntityTag) : Int {
        return tryGetEntityTagId(tag) ?: throw InternalException("EntityTag has no EntityId")
    }

    private fun calculateTagHeight(tagIndex: Int): Double {
        return this._entity.eyeLocation.y + 0.475 + 0.275 * tagIndex
    }

    private fun removePlayerFromAllTags(player: Player){
        for ((tag, playerList) in entityTagPlayers) {
            val entityId = getEntityTagId(tag)
            playerList.remove(player)
            player.PlayerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
        }
    }

    private fun generateDataWatcher(tag: EntityTag, player: Player) : DataWatcher {
        val entityWatcher = DataWatcherUtil.getDataWatcher<EntityArmorStand>()
        val itemMap = entityWatcher.getItems()

        for ((index, item) in itemMap){
            when(index){
                11 -> (item as DataWatcher.Item<Byte>).setValue(ENTITY_BYTE_ARMOR_STAND_MARKER)
                5 -> (item as DataWatcher.Item<Boolean>).setValue(true)
                3 -> (item as DataWatcher.Item<Boolean>).setValue(true)
                2 -> (item as DataWatcher.Item<String>).setValue(tag.doFormatText(player))
                0 -> (item as DataWatcher.Item<Byte>).setValue(ENTITY_BYTE_ENTITY_INVISIBLE)
            }
        }
        return entityWatcher
    }

    private fun generateSpawnPacket(tag: EntityTag, player: Player) : Packet<*> {
        val pos = Entity.location

        val packet = PacketPlayOutSpawnEntityLiving()
        packet.setEntityId(getEntityTagId(tag))
        packet.setEntityType<EntityArmorStand>()
        packet.setUniqueId(UUID.randomUUID())
        packet.setPosition(pos.x, pos.y, pos.z)
        packet.setDataWatcher(generateDataWatcher(tag, player))

        return packet
    }
}
