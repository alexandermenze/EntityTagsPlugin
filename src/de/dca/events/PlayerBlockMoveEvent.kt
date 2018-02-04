package de.dca.events

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerBlockMoveEvent : PlayerEvent, Cancellable {

    companion object {
        private val handlers: HandlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    private val from: BlockLocation
    private val to: BlockLocation
    private var cancelled = false

    val From: BlockLocation
        get() = from

    val To: BlockLocation
        get() = to

    constructor(p: Player, from: BlockLocation, to: BlockLocation)
            : super(p)
    {
        this.from = from
        this.to = to
    }

    override fun isCancelled() : Boolean {
        return cancelled
    }

    override fun setCancelled(cancelled: Boolean) {
        this.cancelled = cancelled
    }

    override fun getHandlers() : HandlerList {
        return Companion.handlers
    }

    class BlockLocation {

        companion object {

            fun fromLocation(loc: Location) : BlockLocation {
                return BlockLocation(loc.world, loc.blockX, loc.blockY, loc.blockZ)
            }
        }

        val World: World
        val X: Int
        val Y: Int
        val Z: Int

        constructor(world: World, x: Int, y: Int, z: Int){
            this.World = world
            this.X = x
            this.Y = y
            this.Z = z
        }

        fun toLocation() : Location {
            return Location(World, X.toDouble(), Y.toDouble(), Z.toDouble())
        }
    }
}