package de.dca.entitytags

import de.dca.entitytags.api.EntityTags
import de.dca.entitytags.entitytracker.CustomEntityTrackerInjector
import de.dca.entitytags.listeners.EntityTagsEventManager
import de.dca.events.CustomEventManager
import org.bukkit.plugin.java.JavaPlugin

class EntityTagsPlugin : JavaPlugin() {

    private lateinit var customEntityTrackerInjector: CustomEntityTrackerInjector
    private lateinit var entityTagsEventManager: EntityTagsEventManager
    private lateinit var customEventManager: CustomEventManager

    override fun onEnable() {
        customEventManager = CustomEventManager()
        customEventManager.onEnable(this)

        customEntityTrackerInjector = CustomEntityTrackerInjector(this)
        customEntityTrackerInjector.injectInLoadedWorlds()

        entityTagsEventManager = EntityTagsEventManager(this)
    }

    override fun onDisable() {
        customEntityTrackerInjector.dispose()
        entityTagsEventManager.dispose()

        customEventManager.onDisable()

        EntityTags.globalDispose()
    }
}