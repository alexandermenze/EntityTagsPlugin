package de.dca.entitytags.extensions

import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun Field.isStatic() : Boolean {
    return (this.modifiers and Modifier.STATIC == Modifier.STATIC)
}