package de.dca.entitytags.api

import org.bukkit.entity.Player
import org.bukkit.util.Vector

abstract class EntityTag {

    companion object {
        @JvmField
        val ZERO_OFFSET = Vector(0, 0, 0)
    }

    fun update(){
        EntityTags.internalUpdateTag(this)
    }

    open fun onAttach(tags: EntityTags){}
    open fun onDetach(tags: EntityTags){}
    open fun isVisibleTo(player: Player) : Boolean { return true }

    open fun calculatePositionOffset(player: Player, position: Vector) : Vector {
        return ZERO_OFFSET
    }

    abstract fun doFormatText(player: Player) : String
}