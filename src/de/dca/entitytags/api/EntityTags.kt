package de.dca.entitytags.api

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class EntityTags private constructor(entity: LivingEntity) {

    companion object {

        val VIEW_DISTANCE = 30
        private val viewDistanceSquared = VIEW_DISTANCE * VIEW_DISTANCE

        private val entityTagsMap : HashMap<LivingEntity, EntityTags> = HashMap()

        fun internalUpdateTag(tag: EntityTag){


        }

    }

    private val entity: LivingEntity = entity
    private val entityTags: LinkedHashMap<EntityTag, Int> = LinkedHashMap()
    private val entityTagPlayers: LinkedHashMap<EntityTag, List<Player>> = LinkedHashMap()

    private val tmpPlayerMap: HashMap<Player, Int> = HashMap()


}