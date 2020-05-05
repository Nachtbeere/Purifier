package net.nachtbeere.minecraft.purifier.logics

import net.nachtbeere.minecraft.purifier.Constants
import net.nachtbeere.minecraft.purifier.models.CurrentTimeModel
import net.nachtbeere.minecraft.purifier.models.GameTimeModel
import net.nachtbeere.minecraft.purifier.models.GameWorldsModel
import org.bukkit.World
import org.eclipse.jetty.http.HttpStatus

typealias HttpStatusCode = Int

class PurifierWorldLogic : PurifierLogicBase() {
    private fun world(targetName: String): World? {
        var target: World? = null
        minecraftServer.worlds.iterator().forEach { w ->
            if (w.name == targetName) {
                target = w
            }
        }
        return target
    }

    fun currentTimes(): ArrayList<CurrentTimeModel>? {
        return this.futureTask {
            val worlds = arrayListOf<CurrentTimeModel>()
            minecraftServer.worlds.iterator().forEach { w ->
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
            worlds
        } as ArrayList<CurrentTimeModel>?
    }

    fun time(worldName: String): CurrentTimeModel? {
        return this.futureTask {
            val targetWorld: World? = world(worldName)
            if (targetWorld != null) {
                CurrentTimeModel(
                    worldName = targetWorld.name,
                    currentTime = GameTimeModel(
                        time = targetWorld.time,
                        age = targetWorld.fullTime
                    )
                )
            } else {
                CurrentTimeModel(
                    worldName = "",
                    currentTime = GameTimeModel(
                        time = 0,
                        age = 0
                    )
                )
            }
        } as CurrentTimeModel?
    }

    fun setTime(worldName: String, moment: String): HttpStatusCode? {
        return this.futureTask {
            var status = HttpStatus.OK_200
            val targetWorld: World? = world(worldName)
            if (targetWorld != null) {
                when (moment) {
                    "day" -> targetWorld.time = Constants.time.day
                    "noon" -> targetWorld.time = Constants.time.noon
                    "night" -> targetWorld.time = Constants.time.night
                    "midnight" -> targetWorld.time = Constants.time.midnight
                    else -> {
                        status = HttpStatus.BAD_REQUEST_400
                    }
                }
            } else {
                status = HttpStatus.NOT_FOUND_404
            }
            status
        } as HttpStatusCode?
    }

    fun setManualTime(worldName: String, time: Long): HttpStatusCode? {
        return this.futureTask {
            val targetWorld: World? = world(worldName)
            if (targetWorld != null) {
                if (time < Constants.time.min || time > Constants.time.max) {
                    HttpStatus.BAD_REQUEST_400
                } else {
                    targetWorld.time = time
                    HttpStatus.OK_200
                }
            } else {
                HttpStatus.NOT_FOUND_404
            }
        } as HttpStatusCode?
    }

    fun toggleStorm(worldName: String): HttpStatusCode? {
        return this.futureTask {
            val targetWorld: World? = world(worldName)
            if (targetWorld != null) {
                targetWorld.setStorm(!targetWorld.hasStorm())
                HttpStatus.OK_200
            } else {
                HttpStatus.NOT_FOUND_404
            }
        } as HttpStatusCode?
    }

    fun worldsInfo() {
        this.futureTask {
            minecraftServer.worlds.iterator().forEach { w ->
            }
        }
    }

    fun worldInfo(targetName: String) {
        this.futureTask {
            val targetWorld: World? = world(targetName)
            if (targetWorld != null) {
//                targetWorld.

            }
        }
    }
}