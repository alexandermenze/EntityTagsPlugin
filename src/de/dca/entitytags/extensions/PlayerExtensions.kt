package de.dca.entitytags.extensions

import de.dca.entitytags.data.PlayerNetworkWrapper
import org.bukkit.entity.Player

val Player.NetworkWrapper : PlayerNetworkWrapper
  get() = PlayerNetworkWrapper(this)