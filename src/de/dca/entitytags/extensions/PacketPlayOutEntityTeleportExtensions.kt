package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityTeleport
import java.lang.reflect.Field

object PacketPlayOutEntityTeleportHelper {

    private val fieldEntityId: Field = getPrivateField("a")
    private val fieldOnGround: Field = getPrivateField("g")
    private val fieldPosX: Field = getPrivateField("b")
    private val fieldPosY: Field = getPrivateField("c")
    private val fieldPosZ: Field = getPrivateField("d")

    init {
        fieldEntityId.isAccessible = true
        fieldOnGround.isAccessible = true
        fieldPosX.isAccessible = true
        fieldPosY.isAccessible = true
        fieldPosZ.isAccessible = true
    }

    private fun getPrivateField(name: String) : Field {
        return PacketPlayOutEntityTeleport::class.java.getDeclaredField(name)
    }

    fun setEntityId(packet: PacketPlayOutEntityTeleport, value: Int){
        fieldEntityId.set(packet, value)
    }

    fun setOnGround(packet: PacketPlayOutEntityTeleport, value: Boolean){
        fieldOnGround.set(packet, value)
    }

    fun setX(packet: PacketPlayOutEntityTeleport, value: Double){
        fieldPosX.set(packet, value)
    }

    fun setY(packet: PacketPlayOutEntityTeleport, value: Double){
        fieldPosY.set(packet, value)
    }

    fun setZ(packet: PacketPlayOutEntityTeleport, value: Double){
        fieldPosZ.set(packet, value)
    }
}

fun PacketPlayOutEntityTeleport.setEntityId(value: Int){
    PacketPlayOutEntityTeleportHelper.setEntityId(this, value)
}

fun PacketPlayOutEntityTeleport.setOnGround(value: Boolean){
    PacketPlayOutEntityTeleportHelper.setOnGround(this, value)
}

fun PacketPlayOutEntityTeleport.setX(value: Double){
    PacketPlayOutEntityTeleportHelper.setX(this, value)
}

fun PacketPlayOutEntityTeleport.setY(value: Double){
    PacketPlayOutEntityTeleportHelper.setY(this, value)
}

fun PacketPlayOutEntityTeleport.setZ(value: Double){
    PacketPlayOutEntityTeleportHelper.setZ(this, value)
}