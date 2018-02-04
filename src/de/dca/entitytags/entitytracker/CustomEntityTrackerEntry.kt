package de.dca.entitytags.entitytracker

import de.dca.entitytags.api.EntityTags
import de.dca.entitytags.util.ReflectionUtil
import net.minecraft.server.v1_12_R1.*
import org.bukkit.entity.LivingEntity

class CustomEntityTrackerEntry
    : EntityTrackerEntry {

    companion object {

        fun copyFrom(oldEntry: EntityTrackerEntry) : CustomEntityTrackerEntry {
            val newEntry = CustomEntityTrackerEntry(oldEntry.b())
            try {
                ReflectionUtil.copyObjectInto(oldEntry, newEntry, EntityTrackerEntry::class.java)
            }catch (ex: Exception){
                throw RuntimeException("Could not create custom entity tracker entry!", ex)
            }
            return newEntry
        }

    }

    constructor(entity: Entity, i: Int, j: Int, k: Int, flag: Boolean)
        : super(entity, i, j, k, flag)

    private constructor(entity: Entity)
        : super(entity, 0, 0, 0, false)

    override fun broadcast(packet: Packet<*>?) {
        super.broadcast(packet)
        handlePacket(packet)
    }

    private fun handlePacket(packet: Packet<*>?){
        if (packet is PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook
                || packet is PacketPlayOutEntity.PacketPlayOutRelEntityMove
                || packet is PacketPlayOutEntityTeleport){

            val entity = this.b() as? EntityLiving ?: return
            val bukkitEntity = entity.bukkitEntity as? LivingEntity ?: return

            if(!EntityTags.has(bukkitEntity))
                return
            EntityTags.of(bukkitEntity).updatePosition()
        }
    }
}