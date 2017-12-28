package de.dca.entitytags.util

import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun Field.isStatic() : Boolean {
    val modifiers = this.modifiers
    return (modifiers and Modifier.STATIC == Modifier.STATIC)
}

fun Field.isFinal() : Boolean {
    val modifiers = this.modifiers
    return (modifiers and Modifier.FINAL == Modifier.FINAL)
}

fun Field.setValueForced(obj: Any, value: Any) {
    ReflectionUtil.setPrivateFinalField(this, obj, value)
}