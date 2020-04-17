package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import net.nachtbeere.minecraft.purifier.*
import org.bukkit.World
import org.eclipse.jetty.http.HttpStatus

fun getWorld(worlds: List<World>, targetName: String): World? {
    var target: World? = null
    worlds.iterator().forEach { w ->
        if (w.name == targetName) {
            target = w
        }
    }
    return target
}

object PurifierWorldsController : PurifierControllerBase() {
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
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetTimeModel::class)]),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun setTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val req = ctx.bodyAsClass(SetTimeModel::class.java)
        var result = "SUCCESS"
        val statusCode = this.futureTask {
            val targetWorld: World? = getWorld(bukkitServer.worlds, paramWorld)
            if (targetWorld != null) {
                var status = HttpStatus.OK_200
                when (req.moment) {
                    "day" -> targetWorld.time = Constants.time.day
                    "noon" -> targetWorld.time = Constants.time.noon
                    "night" -> targetWorld.time = Constants.time.night
                    "midnight" -> targetWorld.time = Constants.time.midnight
                    else -> {
                        result = "FAILED"
                        status = HttpStatus.BAD_REQUEST_400
                    }
                }
                status
            } else {
                result = "FAILED"
                HttpStatus.NOT_FOUND_404
            }
        }
        ctx.status(statusCode as Int)
        ctx.json(CommonResponseModel(result = result))
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetManualTimeModel::class)]),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun setManualTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        var result = "SUCCESS"
        val req = ctx.bodyAsClass(SetManualTimeModel::class.java)
        val statusCode = this.futureTask {
            val targetWorld: World? = getWorld(bukkitServer.worlds, paramWorld)
            if (targetWorld != null) {
                if (req.time < 0.toLong() || req.time > 24000.toLong()) {
                    result = "FAILED"
                    HttpStatus.BAD_REQUEST_400
                } else {
                    targetWorld.time = req.time
                    HttpStatus.OK_200
                }
            } else {
                result = "FAILED"
                HttpStatus.NOT_FOUND_404
            }
        }
        ctx.status(statusCode as Int)
        ctx.json(CommonResponseModel(result = result))
    }

    @OpenApi(
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun toggleStorm(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val statusCode = this.futureTask {
            val targetWorld: World? = getWorld(bukkitServer.worlds, paramWorld)
            if (targetWorld != null) {
                targetWorld.setStorm(!targetWorld.hasStorm())
                HttpStatus.OK_200
            } else {
                HttpStatus.NOT_FOUND_404
            }
        }
        ctx.status(statusCode as Int)
        var result = "SUCCESS"
        if (statusCode != HttpStatus.OK_200) {
            result = "FAILED"
        }
        ctx.json(CommonResponseModel(result = result))
    }
}