package de.dca.entitytags.api

import de.dca.entitytags.util.EntityIdRepository
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
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
            val entityId = entityTags.remove(tag)
            val players = entityTagPlayers.remove(tag) ?: return false

            for (p in players) {
                try {
                    (p as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
                } catch (ex: Exception) {
                    ex.printStackTrace() // Ignore packet errors
                }

            }
            EntityIdRepository.free(entityId)
            tag.onDetach(this)
            return true
        }
        return false
    }

    fun size(): Int {
        return entityTags.size
    }

    fun update(){

    }

    fun update(tag: EntityTag){

    }

    fun updatePosition(){

    }

    fun dispose(){

    }
}