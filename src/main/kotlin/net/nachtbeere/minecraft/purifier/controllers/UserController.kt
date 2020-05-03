package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import net.nachtbeere.minecraft.purifier.PurifierGameMode
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.eclipse.jetty.http.HttpStatus

object PurifierUserController : PurifierControllerBase() {
    /*
    TODO: /tp function
    TODO: /give function
    */
    fun user(player: Player?): UserModel {
        return if (player != null) {
            UserModel(
                username = player.displayName,
                locale = player.locale,
                gamemode = player.gameMode.name,
                level = player.level,
                exp = player.exp,
                hunger = player.foodLevel,
                vital = player.health,
                location = LocationModel(
                    world = player.location.world!!.name,
                    x = player.location.x,
                    y = player.location.y,
                    z = player.location.z
                )
            )
        } else {
            UserModel(
                username = "",
                locale = "",
                gamemode = "",
                level = 0,
                exp = 0.toFloat(),
                hunger = 0,
                vital = 0.toDouble(),
                location = LocationModel(
                    world = "",
                    x = 0.toDouble(),
                    y = 0.toDouble(),
                    z = 0.toDouble()
                )
            )
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(UsersModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun onlineUsers(ctx: Context)  {
        val payload = futureTask {
            val currentUsers = bukkitServer.onlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                    user(p)
                )
            }
            UsersModel(
                    total=currentUsers.size,
                    users=users
            )
        }
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(UsersModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun offlineUsers(ctx: Context) {
        val payload = futureTask {
            val currentUsers = bukkitServer.offlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                    user(null)
                )
            }
            UsersModel(
                total=currentUsers.size,
                users=users
            )
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
            content = [OpenApiContent(SetUserGameModeModel::class)]),
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(SingleUserModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun setUserGameMode(ctx: Context) {
        val paramUser = ctx.pathParam(":username")
        val req = ctx.bodyAsClass(SetUserGameModeModel::class.java)
        val payload = futureTask {
            val currentUser = bukkitServer.getPlayer(paramUser)
            if (currentUser != null) {
                currentUser.gameMode = GameMode.valueOf(PurifierGameMode.valueOf(req.gamemode).toString())
            }
            SingleUserModel(user=user(currentUser))
        }
        if (payload != null) {
            if (payload is SingleUserModel && payload.user.username == paramUser) {
                ctx.json(payload)
            } else {
                ctx.status(HttpStatus.NOT_FOUND_404)
                ctx.json(this.failedResponse())
            }
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }
}