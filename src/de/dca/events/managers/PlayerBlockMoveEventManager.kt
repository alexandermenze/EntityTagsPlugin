package de.dca.events.managers

import de.dca.events.PlayerBlockMoveEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin

class PlayerBlockMoveEventManager : Listener {

    constructor(plugin: Plugin){
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun dispose(){
        HandlerList.unregisterAll(this)
    }

    @EventHandler(ignoreCancelled = true)
    private fun onPlayerMove(event: PlayerMoveEvent){
        if (event.from.blockX != event.to.blockX
            || event.from.blockZ != event.to.blockZ
            || event.from.blockY != event.to.blockY)
        {
            val playerBlockMoveEvent = PlayerBlockMoveEvent(event.player,
                    PlayerBlockMoveEvent.BlockLocation.fromLocation(event.from),
                    PlayerBlockMoveEvent.BlockLocation.fromLocation(event.to))

            Bukkit.getPluginManager().callEvent(playerBlockMoveEvent)

            if(playerBlockMoveEvent.isCancelled)
                event.isCancelled = true
        }
    }
}