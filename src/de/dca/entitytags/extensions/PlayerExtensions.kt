package de.dca.entitytags.extensions

import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

val Player.NetworkWrapper : PlayerConnection
  get() = (this as CraftPlayer).handle.playerConnection