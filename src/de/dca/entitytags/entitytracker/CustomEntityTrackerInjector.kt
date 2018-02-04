package de.dca.entitytags.entitytracker

import de.dca.entitytags.util.ReflectionUtil
import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent
import org.bukkit.plugin.Plugin
import java.io.Closeable




class CustomEntityTrackerInjector : Listener {

    constructor(plugin: Plugin){
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    fun injectInLoadedWorlds() {
        for (world in Bukkit.getWorlds()){
            inject(world)
        }
    }

    fun inject(world: World){
        inject((world as CraftWorld).handle)
    }

    fun inject(worldServer: WorldServer) : CustomEntityTracker {
        val oldTracker = worldServer.tracker
        if(oldTracker is CustomEntityTracker)
            return oldTracker

        val newTracker = CustomEntityTracker(worldServer)
        try {
            ReflectionUtil.copyObjectInto(src = oldTracker, dest = newTracker,
                    typeToCopy = EntityTracker::class.java)
        }catch (ex: IllegalAccessException){
            val worldName = worldServer.dataManager.directory.name
            throw InjectException("Could not inject in world '$worldName'", ex)
        }
        worldServer.tracker = newTracker
        newTracker.checkTrackerEntries()
        return newTracker
    }

    fun dispose(){
        HandlerList.unregisterAll(this)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onWorldLoad(e: WorldLoadEvent){
        inject(e.world)
    }
}