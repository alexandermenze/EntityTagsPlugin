package de.dca.entitytags.api

import de.dca.entitytags.extensions.NetworkWrapper
import de.dca.entitytags.extensions.PlayerConnection
import de.dca.entitytags.util.EntityIdRepository
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

class EntityTags {

    companion object {

        private val VIEW_DISTANCE = 30
        private val viewDistanceSquared = VIEW_DISTANCE * VIEW_DISTANCE

        private val entityTagsMap : HashMap<LivingEntity, EntityTags> = HashMap()

        fun internalUpdateTag(tag: EntityTag) {

        }

        fun internalPlayerLeft(p: Player){

        }

        fun internalPlayerUpdate(p: Player){

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
    private val entityTagPlayers: LinkedHashMap<EntityTag, List<Player>> = LinkedHashMap()

    private val tmpPlayerMap: HashMap<Player, Int> = HashMap()
    private val tmpDataWatcher: DataWatcher = DataWatcher(null)

    val Size: Int
        get() = entityTags.size

    private val Alive: Boolean
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
                p.NetworkWrapper.sendDestroyEntity(entityId)
            }

            EntityIdRepository.free(entityId)
            tag.onDetach(this)
            return true
        }
        return false
    }

    fun update(){

    }

    fun update(tag: EntityTag){

    }

    fun updatePosition(){

    }

    fun dispose(){
        clear()
        EntityTags.remove(this._entity)
    }

    fun clear() {
        for ((entityTagObject, players) in this.entityTagPlayers) {
            val entityId = getEntityTagId(entityTagObject) ?: continue

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

    private fun getEntityTagId(tag: EntityTag) : Int? {
        return entityTags[tag]
    }

    private fun calculateTagHeight(tagIndex: Int): Double {
        return this._entity.eyeLocation.y + 0.475 + 0.275 * tagIndex
    }
}