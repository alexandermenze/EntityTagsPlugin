package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.DataWatcherObject
import net.minecraft.server.v1_12_R1.Entity
import java.lang.reflect.Field

private object DataWatcherHelper {

    private val ENTITY_FIELD: Field

    init {
        ENTITY_FIELD = findEntityField()
    }

    private fun findEntityField() : Field {
        try {
            return DataWatcher::class.java.getDeclaredField("c")
        }catch (ex: IllegalAccessException){
            throw RuntimeException(ex)
        }
    }

    fun getEntity(watcher: DataWatcher) : Entity {
        return ENTITY_FIELD.get(watcher) as Entity
    }

    fun cloneDataWatcher(source: DataWatcher) : DataWatcher {
        val clone = DataWatcher(source.getEntity())
        val watcherObjects = source.c() ?: return clone
        for (value in watcherObjects){
            insert(clone, value as DataWatcher.Item<Any>)
        }
        return clone
    }

    private fun <T> insert(watcher: DataWatcher, item: DataWatcher.Item<T>){
        watcher[item.a()] = item.b()
    }

}

fun DataWatcher.getEntity() : Entity {
    return DataWatcherHelper.getEntity(this)
}

fun DataWatcher.deepClone() : DataWatcher {
    return DataWatcherHelper.cloneDataWatcher(this)
}