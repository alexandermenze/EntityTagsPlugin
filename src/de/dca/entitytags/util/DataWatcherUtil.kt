package de.dca.entitytags.util

import de.dca.entitytags.extensions.deepClone
import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.World

object DataWatcherUtil {

    private val watcherMap: MutableMap<Class<out Entity>, DataWatcher> = HashMap()

    val LazyDataWatchers: MutableMap<Class<out Entity>, DataWatcher>
        get() = watcherMap

    inline fun <reified T : Entity> getDataWatcher() : DataWatcher {
        val lazyWatcher = LazyDataWatchers[T::class.java]
        if (lazyWatcher != null)
            return lazyWatcher.deepClone()


        val dataWatcher = createDataWatcher<T>()
        LazyDataWatchers[T::class.java] = dataWatcher
        return dataWatcher.deepClone()
    }

    inline fun <reified T : Entity> createDataWatcher() : DataWatcher {
        val worldClass = World::class.java
        val constructor = T::class.java.getConstructor(worldClass)
                ?: throw RuntimeException("No single parameter constructor found for type '${T::class.java.name}' with argument '${worldClass.name}'")

        constructor.isAccessible = true
        val entityObject: T = constructor.newInstance(null)
        return entityObject.dataWatcher
    }
}