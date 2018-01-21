package de.dca.entitytags.data

import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class PlayerNetworkWrapper {

    private val playerConnection : PlayerConnection

    constructor(player: Player){
        playerConnection = (player as CraftPlayer).handle.playerConnection
    }

    fun sendDestroyEntity(entityId: Int){
        playerConnection.sendPacket(PacketPlayOutEntityDestroy(entityId))
    }
}