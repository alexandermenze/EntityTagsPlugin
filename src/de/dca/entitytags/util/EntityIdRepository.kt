package de.dca.entitytags.util

import net.minecraft.server.v1_12_R1.Entity
import java.lang.reflect.Field

object EntityIdRepository {

    private const val ENTITY_COUNT_FIELD_NAME = "entityCount"

    private val entityCountField : Field
    private val idMap : LinkedHashMap<Int, Boolean>

    init {
        idMap = LinkedHashMap()
        entityCountField = findEntityCountField()
        entityCountField.isAccessible = true
    }

    private fun findEntityCountField() : Field {
        return Entity::class.java.getDeclaredField(ENTITY_COUNT_FIELD_NAME)
    }

    private fun reserveEntityId() : Int {
        val id = entityCountField.getInt(null)
        entityCountField.setInt(null, id + 1)
        return id
    }

    fun reserve() : Int {
        for(id in idMap){
            if (id.value)
                continue

            id.setValue(true)
            return id.key
        }
        val id = reserveEntityId()
        idMap.put(id, true)
        return id
    }

    fun free(id: Int) {
        if (idMap.containsKey(id))
            idMap[id] = false
    }

}