package de.dca.entitytags.listeners

import de.dca.entitytags.api.EntityTags
import de.dca.events.PlayerBlockMoveEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin

class EntityTagsEventManager : Listener {

    constructor(plugin: Plugin){
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun dispose(){
        HandlerList.unregisterAll(this)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onEntityDie(event: EntityDeathEvent){
        if(!event.entity.isDead)
            return

        EntityTags.remove(event.entity)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onPlayerBlockMove(event: PlayerBlockMoveEvent){
        EntityTags.internalPlayerUpdate(event.player)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        EntityTags.internalPlayerUpdate(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerLeft(event: PlayerQuitEvent){
        handlePlayerLeft(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onPlayerKick(event: PlayerKickEvent){
        handlePlayerLeft(event.player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onPlayerChangedWorlds(event: PlayerChangedWorldEvent){
        EntityTags.internalPlayerUpdate(event.player)
    }

    private fun handlePlayerLeft(player: Player){
        EntityTags.internalPlayerLeft(player)
        EntityTags.remove(player)
    }
}