package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import net.nachtbeere.minecraft.purifier.logics.PurifierUserLogic
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import io.javalin.plugin.openapi.annotations.OpenApiParam
import org.eclipse.jetty.http.HttpStatus

object PurifierUserController : PurifierControllerBase() {
    /*
    TODO: /tp function
    TODO: /give function
    */
    private val userLogic = PurifierUserLogic()

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(UsersModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun user(ctx: Context) {
        val paramUser = ctx.pathParam(":username")
        val payload = userLogic.user(paramUser)
        ctx.json(SingleUserModel(user = payload))
    }

    @OpenApi(
            queryParams = [
                OpenApiParam(
                        name = "find",
                        type = String::class,
                        description = "find online/offline users. default value is online"
                )
            ],
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(UsersModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun users(ctx: Context) {
        val find = ctx.queryParam("find", default = "online")
        val payload = if (find == "offline") {
            userLogic.offlineUsers()
        } else {
            userLogic.onlineUsers()
        }
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            pathParams = [
                OpenApiParam(
                        name = "username"
                )
            ],
            requestBody = OpenApiRequestBody(
                    content = [OpenApiContent(SetUserGameModeModel::class)]
            ),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(SingleUserModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun setUserGameMode(ctx: Context) {
        val paramUser = ctx.pathParam(":username")
        val req = ctx.bodyAsClass(SetUserGameModeModel::class.java)
        val payload = userLogic.setUserGameMode(paramUser, req.gamemode)
        val result = SingleUserModel(user = payload)
        if (result.user?.username == paramUser) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404)
            ctx.json(this.failedResponse())
        }
    }
}