package de.dca.entitytags.util

import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.World

object DataWatcherUtil {

    private val _watcherMap: MutableMap<Class<out Entity>, DataWatcher> = HashMap()

    val LazyDataWatchers: MutableMap<Class<out Entity>, DataWatcher>
        get() = _watcherMap

    inline fun <reified T : Entity> getDataWatcher() : DataWatcher {
        val lazyWatcher = LazyDataWatchers[T::class.java]
        if (lazyWatcher != null)
            return lazyWatcher

        val dataWatcher = createDataWatcher<T>()
        LazyDataWatchers[T::class.java] = dataWatcher
        return dataWatcher
    }

    inline fun <reified T : Entity> createDataWatcher() : DataWatcher {
        val worldClass = World::class.java
        val constructor = T::class.java.getConstructor(worldClass)
                ?: throw RuntimeException("No single parameter constructor found for type '${T::class.java.name}' with argument '${worldClass.name}'")

        val entityObject: T = constructor.newInstance(null)
        return entityObject.dataWatcher
    }
}