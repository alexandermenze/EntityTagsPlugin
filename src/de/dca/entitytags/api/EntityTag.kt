package de.dca.entitytags.api

import org.bukkit.entity.Player
import org.bukkit.util.Vector


val ZERO_OFFSET = Vector(0,0 ,0)

abstract class EntityTag(text: String = "") {

    protected var tagText : String = text

    fun update(){
        EntityTags.internalUpdateTag(this)
    }

    open fun setText(text: String){
        this.tagText = text
        update()
    }

    open fun getText() : String {
        return this.tagText
    }

    open fun onAttach(tags: EntityTags){}
    open fun onDetach(tags: EntityTags){}

    abstract fun isVisibleTo(player: Player)
    abstract fun doFormatText(player: Player)

    open fun calculatePositionOffset(player: Player, position: Vector) : Vector {
        return ZERO_OFFSET
    }
}