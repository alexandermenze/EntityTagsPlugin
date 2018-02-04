package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.DataWatcher
import net.minecraft.server.v1_12_R1.Entity
import net.minecraft.server.v1_12_R1.EntityTypes
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving
import java.lang.reflect.Field
import java.util.*

object PacketPlayOutSpawnEntityLivingHelper {

    private val fieldEntityId: Field = makeAccessible(getPrivateField("a"))
    private val fieldUniqueId: Field = makeAccessible(getPrivateField("b"))
    private val fieldEntityType: Field = makeAccessible(getPrivateField("c"))
    private val fieldPosX: Field = makeAccessible(getPrivateField("d"))
    private val fieldPosY: Field = makeAccessible(getPrivateField("e"))
    private val fieldPosZ: Field = makeAccessible(getPrivateField("f"))
    private val fieldDataWatcher: Field = makeAccessible(getPrivateField("m"))

    private fun getPrivateField(name: String) : Field {
        return PacketPlayOutSpawnEntityLiving::class.java.getDeclaredField(name)
    }

    private fun makeAccessible(f: Field) : Field {
        f.isAccessible = true
        return f
    }

    fun setEntityId(packet: PacketPlayOutSpawnEntityLiving, id: Int){
        fieldEntityId.set(packet, id)
    }

    fun setUniqueId(packet: PacketPlayOutSpawnEntityLiving, id: UUID){
        fieldUniqueId.set(packet, id)
    }

    fun setEntityType(packet: PacketPlayOutSpawnEntityLiving, type: Int){
        fieldEntityType.set(packet, type)
    }

    fun setEntityType(packet: PacketPlayOutSpawnEntityLiving, type: Class<out Entity>){
        fieldEntityType.set(packet, EntityTypes.b.a(type))
    }

    fun setPosition(packet: PacketPlayOutSpawnEntityLiving, x: Double, y: Double, z: Double){
        fieldPosX.set(packet, x)
        fieldPosY.set(packet, y)
        fieldPosZ.set(packet, z)
    }

    fun setDataWatcher(packet: PacketPlayOutSpawnEntityLiving, dataWatcher: DataWatcher){
        fieldDataWatcher.set(packet, dataWatcher)
    }

}

fun PacketPlayOutSpawnEntityLiving.setEntityId(id: Int){
    PacketPlayOutSpawnEntityLivingHelper.setEntityId(this, id)
}

fun PacketPlayOutSpawnEntityLiving.setUniqueId(id: UUID){
    PacketPlayOutSpawnEntityLivingHelper.setUniqueId(this, id)
}

fun PacketPlayOutSpawnEntityLiving.setEntityType(type: Int){
    PacketPlayOutSpawnEntityLivingHelper.setEntityType(this, type)
}

fun PacketPlayOutSpawnEntityLiving.setEntityType(type: Class<out Entity>){
    PacketPlayOutSpawnEntityLivingHelper.setEntityType(this, type)
}

inline fun <reified T : Entity> PacketPlayOutSpawnEntityLiving.setEntityType(){
    setEntityType(T::class.java)
}

fun PacketPlayOutSpawnEntityLiving.setPosition(x: Double, y: Double, z: Double){
    PacketPlayOutSpawnEntityLivingHelper.setPosition(this, x, y, z)
}

fun PacketPlayOutSpawnEntityLiving.setDataWatcher(dataWatcher: DataWatcher){
    PacketPlayOutSpawnEntityLivingHelper.setDataWatcher(this, dataWatcher)
}