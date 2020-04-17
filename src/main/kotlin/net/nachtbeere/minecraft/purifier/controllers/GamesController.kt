package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import net.nachtbeere.minecraft.purifier.*
import org.bukkit.World

object PurifierGameController : PurifierControllerBase() {
    fun currentTime(ctx: Context)  {
        val payload = this.futureTask {
            val worlds = arrayListOf<CurrentTimeModel>()
            bukkitServer.worlds.iterator().forEach { w ->
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
        if (payload != null) {
            ctx.json(payload)
        }
    }

    fun setTime() {
        // TODO: time parameter. temporary set to day
        val payload = CommonResponseModel(result = "SUCCESS")
        this.futureTask {
            var world: World? = null
            bukkitServer.worlds.iterator().forEach { w ->
                if (w.name == "world") {
                    world = w
                }
            }
            if (world != null) {
                world!!.time = (1000).toLong()
            }
        }
    }
}
