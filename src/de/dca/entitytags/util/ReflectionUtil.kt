package de.dca.entitytags.util

import de.dca.entitytags.extensions.isStatic
import sun.misc.Unsafe
import java.lang.reflect.Field
import java.lang.reflect.Modifier

object ReflectionUtil {

    val unsafeInstance : Unsafe
    private val fieldModifiersField : Field

    init {
        unsafeInstance = findUnsafe()
        fieldModifiersField = findModifierField()
        fieldModifiersField.isAccessible = true
    }

    private fun findUnsafe() : Unsafe {
        try {
            val field = Unsafe::class.java.getDeclaredField("theUnsafe")
            field.isAccessible = true
            return (field.get(null) as Unsafe)
        }catch (ex: Exception){
            throw RuntimeException(ex)
        }
    }

    private fun findModifierField() : Field {
        try {
            return Field::class.java.getDeclaredField("modifiers")
        }catch (ex: Exception){
            throw RuntimeException(ex)
        }
    }

    fun setPrivateFinalField(field: Field, obj: Any, value: Any){
        field.isAccessible = true
        val modifiers = fieldModifiersField.getInt(field)
        fieldModifiersField.setInt(field, modifiers and Modifier.FINAL.inv())
        field.set(obj, value)
    }

    inline fun <reified T> allocateObject() : T {
        return (unsafeInstance.allocateInstance(T::class.java) as T)
    }

    fun <T : Any> copyObject(src: T) : T {
        return copyObject(src, src::class.java)
    }

    fun <T : Any> copyObject(src: T, typeToCopy: Class<*>) : T {
        val out = unsafeInstance.allocateInstance(typeToCopy) as T
        copyObjectInto(src, out)
        return out
    }

    fun <T : Any> copyObjectInto(src: T, dest: T){
        copyObjectInto(src, dest, src::class.java)
    }

    fun <T : Any> copyObjectInto(src: T, dest: T, typeToCopy: Class<*>) {
        for (field in typeToCopy.declaredFields){
            if (field.isStatic())
                continue
            field.isAccessible = true
            this.setPrivateFinalField(field, dest, field.get(src))
        }
    }
}