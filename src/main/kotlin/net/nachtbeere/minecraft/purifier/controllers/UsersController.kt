package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import net.nachtbeere.minecraft.purifier.*
import org.eclipse.jetty.http.HttpStatus

object PurifierUsersController : PurifierControllerBase() {
    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(), content = [OpenApiContent(UsersModel::class)]),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(), content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun onlineUsers(ctx: Context)  {
        val payload = futureTask {
            val currentUsers = bukkitServer.onlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                        UserModel(
                                username = p.displayName,
                                locale = p.locale,
                                level = p.level,
                                exp = p.exp,
                                hunger = p.foodLevel,
                                vital = p.health,
                                location = LocationModel(
                                        world = p.location.world!!.name,
                                        x = p.location.x,
                                        y = p.location.y,
                                        z = p.location.z
                                )
                        )
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
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(), content = [OpenApiContent(UsersModel::class)]),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(), content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun offlineUsers(ctx: Context) {
        val payload = futureTask {
            val currentUsers = bukkitServer.offlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                    UserModel(
                        username = "",
                        locale = "",
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
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }
}