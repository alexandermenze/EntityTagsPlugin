package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.DataWatcherObject
import net.minecraft.server.v1_12_R1.Entity
import java.lang.reflect.Field

private object DataWatcherHelper {

    private val ENTITY_FIELD: Field
    private val WATCHER_ITEM_MAP_FIELD: Field

    init {
        ENTITY_FIELD = findEntityField()
        ENTITY_FIELD.isAccessible = true

        WATCHER_ITEM_MAP_FIELD = findWatcherItemMapField()
        WATCHER_ITEM_MAP_FIELD.isAccessible = true
    }

    private fun findEntityField() : Field {
        try {
            return DataWatcher::class.java.getDeclaredField("c")
        }catch (ex: IllegalAccessException){
            throw RuntimeException(ex)
        }
    }

    private fun findWatcherItemMapField() : Field {
        try{
            return DataWatcher::class.java.getDeclaredField("d")
        }catch (ex: IllegalAccessException){
            throw RuntimeException(ex)
        }
    }

    fun getEntity(watcher: DataWatcher) : Entity {
        return ENTITY_FIELD.get(watcher) as Entity
    }

    fun getItemMap(watcher: DataWatcher) : MutableMap<Int, DataWatcher.Item<*>> {
        return WATCHER_ITEM_MAP_FIELD.get(watcher) as MutableMap<Int, DataWatcher.Item<*>>
    }

    fun cloneDataWatcher(source: DataWatcher) : DataWatcher {
        val clone = DataWatcher(source.getEntity())
        val watcherObjects = source.c() ?: return clone

        val watcherItemMap = getItemMap(clone)
        for (item in watcherObjects) {
            watcherItemMap[item.a().a()] = item.d()
        }
        return clone
    }
}

fun DataWatcher.getEntity() : Entity {
    return DataWatcherHelper.getEntity(this)
}

fun DataWatcher.deepClone() : DataWatcher {
    return DataWatcherHelper.cloneDataWatcher(this)
}

fun DataWatcher.getItems() : Map<Int, DataWatcher.Item<*>>{
    return DataWatcherHelper.getItemMap(this)
}