package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import net.nachtbeere.minecraft.purifier.*
import org.eclipse.jetty.websocket.api.StatusCode

object PurifierUsersController : PurifierControllerBase() {
    @OpenApi(
        responses = [
            OpenApiResponse(status = "200", content = [OpenApiContent(CurrentUsersModel::class)]),
            OpenApiResponse(status = "500", content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun currentUsers(ctx: Context)  {
        this.log("Current User List Request Accepted.")
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
            CurrentUsersModel(
                    total=currentUsers.size,
                    users=users
            )
        }
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(StatusCode.SERVER_ERROR)
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }
}