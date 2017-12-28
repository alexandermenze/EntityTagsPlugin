package de.dca.entitytags.extensions

import de.dca.entitytags.util.ReflectionUtil
import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun Field.isStatic() : Boolean {
    return (this.modifiers and Modifier.STATIC == Modifier.STATIC)
}

fun Field.setValueForced(obj: Any, value: Any){
    ReflectionUtil.setPrivateFinalField(this, obj, value)
}