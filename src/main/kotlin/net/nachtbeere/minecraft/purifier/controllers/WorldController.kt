package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.Constants
import net.nachtbeere.minecraft.purifier.models.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus
import org.bukkit.World

fun getWorld(worlds: List<World>, targetName: String): World? {
    var target: World? = null
    worlds.iterator().forEach { w ->
        if (w.name == targetName) {
            target = w
        }
    }
    return target
}

object PurifierWorldController : PurifierControllerBase() {
    @OpenApi(
            responses = [
                OpenApiResponse(status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(GameWorldsModel::class)]),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)])
            ]
    )
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
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetTimeModel::class)]),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun setTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val req = ctx.bodyAsClass(SetTimeModel::class.java)
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
                        status = HttpStatus.BAD_REQUEST_400
                    }
                }
                status
            } else {
                HttpStatus.NOT_FOUND_404
            }
        }
        ctx.status(statusCode as Int)
        if (statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetManualTimeModel::class)]),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun setManualTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val req = ctx.bodyAsClass(SetManualTimeModel::class.java)
        val statusCode = this.futureTask {
            val targetWorld: World? = getWorld(bukkitServer.worlds, paramWorld)
            if (targetWorld != null) {
                if (req.time < Constants.time.min || req.time > Constants.time.max) {
                    HttpStatus.BAD_REQUEST_400
                } else {
                    targetWorld.time = req.time
                    HttpStatus.OK_200
                }
            } else {
                HttpStatus.NOT_FOUND_404
            }
        }
        ctx.status(statusCode as Int)
        if (statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
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
        if (statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(GameWorldsModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun worlds(ctx: Context) {
        val statusCode = this.futureTask {
            bukkitServer.worlds.iterator().forEach { w ->
            }
        }
        ctx.status(statusCode as Int)
        if (statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
    }
}
