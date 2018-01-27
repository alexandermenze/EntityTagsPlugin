package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.DataWatcher

fun <T> DataWatcher.Item<T>.setValue(value: T){
    this.a(value)
}