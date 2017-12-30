package de.dca.entitytags.entitytracker

import net.minecraft.server.v1_12_R1.*
import org.bukkit.World
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import java.lang.reflect.Field
import java.util.*

class CustomEntityTracker
    : EntityTracker {

    private lateinit var superTrackerSet: MutableSet<EntityTrackerEntry>
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

    constructor(world: World)
        : this((world as CraftWorld).handle)

    private fun loadTrackersFromSuper(){
        try {
            superTrackerSet = trackerSetField.get(this) as MutableSet<EntityTrackerEntry>
            superTrackerMap = this.trackedEntities
        }catch (ex: Exception){
            throw RuntimeException(ex)
        }
    }

    fun checkTrackerEntries(){
        loadTrackersFromSuper()
        tmpTrackerSet.clear()

        val iter: MutableIterator<EntityTrackerEntry> = superTrackerSet.iterator()
        while (iter.hasNext()){
            val it = iter.next()
            if (it is CustomEntityTrackerEntry)
                continue

            iter.remove()
            tmpTrackerSet.add(CustomEntityTrackerEntry.copyFrom(it))
        }

        if (tmpTrackerSet.size > 0){
            superTrackerSet.addAll(tmpTrackerSet)
            for (entry in tmpTrackerSet){
                superTrackerMap.a(entry.b().id, entry)
            }
        }
    }

    override fun addEntity(entity: Entity?, i: Int, j: Int, flag: Boolean) {
        super.addEntity(entity, i, j, flag)
        checkTrackerEntries()
    }
}