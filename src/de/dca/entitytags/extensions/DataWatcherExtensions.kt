package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.DataWatcher
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

}

fun DataWatcher.getEntity() : Entity {
    return DataWatcherHelper.getEntity(this)
}