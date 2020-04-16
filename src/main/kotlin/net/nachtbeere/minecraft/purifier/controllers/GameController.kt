package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.*
import org.bukkit.World

class PurifierGameController(private val instance: Purifier) : PurifierControllerBase(instance) {
    fun currentTime(): String {
        val payload = this.futureTask {
            val worlds = arrayListOf<CurrentTimeModel>()
            this.instance.server.worlds.iterator().forEach { w ->
                worlds.add(
                        CurrentTimeModel(
                            worldName = w.name,
                            currentTime = GameTimeModel(
                                    time = w.time,
                                    age = w.fullTime
                        )
                    )
                )
            }
            GameWorldsModel(worlds = worlds)
        }
        return jsonMaker(payload)
    }

    fun setTime(): String {
        // TODO: time parameter. temporary set to day
        val payload = CommonResponseModel(result = "SUCCESS")
        this.futureTask {
            var world: World? = null
            this.instance.server.worlds.iterator().forEach { w ->
                if (w.name == "world") {
                    world = w
                }
            }
            if (world != null) {
                world!!.time = (1000).toLong()
                world!!.
            }
        }
        return jsonMaker(payload)
    }
}
