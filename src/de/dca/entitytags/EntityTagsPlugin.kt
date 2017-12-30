package de.dca.entitytags

import de.dca.entitytags.entitytracker.CustomEntityTrackerInjector
import org.bukkit.plugin.java.JavaPlugin

class EntityTagsPlugin : JavaPlugin() {

    private lateinit var customEntityTrackerInjector: CustomEntityTrackerInjector

    override fun onEnable() {
        customEntityTrackerInjector = CustomEntityTrackerInjector(this)
        customEntityTrackerInjector.injectInLoadedWorlds()
    }

    override fun onDisable() {
        customEntityTrackerInjector.dispose()
    }

}