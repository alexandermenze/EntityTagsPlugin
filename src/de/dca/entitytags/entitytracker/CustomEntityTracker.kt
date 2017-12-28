package de.dca.entitytags.entitytracker

import net.minecraft.server.v1_12_R1.EntityTracker
import net.minecraft.server.v1_12_R1.EntityTrackerEntry
import net.minecraft.server.v1_12_R1.IntHashMap
import net.minecraft.server.v1_12_R1.WorldServer
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import java.lang.reflect.Field
import java.util.*

class CustomEntityTracker
    : EntityTracker {

    private lateinit var superTrackerSet: Set<EntityTrackerEntry>
    private lateinit var superTrackerMap: IntHashMap<EntityTrackerEntry>

    private val tmpTrackerSet: HashSet<EntityTrackerEntry> = HashSet()

    companion object {

        private val trackerSetField: Field

        init {
            trackerSetField = findTrackerSetField()
            trackerSetField.isAccessible = true
        }

        private fun findTrackerSetField() : Field {
            try {
                return EntityTracker::class.java.getDeclaredField("c")
            }catch (ex: Exception){
                throw RuntimeException(ex)
            }
        }

    }

    constructor(worldServer: WorldServer)
        : super(worldServer)
    {
        checkTrackerEntries()
    }

    constructor(world: World)
        : this((world as CraftWorld).handle)

    private fun loadTrackersFromSuper(){
        try {
            superTrackerSet = trackerSetField.get(this) as Set<EntityTrackerEntry>
            superTrackerMap = this.trackedEntities
        }catch (ex: Exception){
            throw RuntimeException(ex)
        }
    }

    private fun checkTrackerEntries(){
        loadTrackersFromSuper()
        tmpTrackerSet.clear()

        val iter: Iterator<EntityTrackerEntry> = superTrackerSet.iterator()
        while (iter.hasNext()){
            val it = iter.next()
            if (it is CustomEntityTrackerEntry)
                continue


        }
    }
}