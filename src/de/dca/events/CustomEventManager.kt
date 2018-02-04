package de.dca.events

import de.dca.events.managers.PlayerBlockMoveEventManager
import org.bukkit.plugin.Plugin

class CustomEventManager {

    private lateinit var playerBlockMoveEventManager: PlayerBlockMoveEventManager

    fun onEnable(plugin: Plugin){
        playerBlockMoveEventManager = PlayerBlockMoveEventManager(plugin)
    }

    fun onDisable(){
        playerBlockMoveEventManager.dispose()
    }

}