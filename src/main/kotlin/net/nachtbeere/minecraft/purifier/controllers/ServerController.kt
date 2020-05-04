package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import net.nachtbeere.minecraft.purifier.logics.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.eclipse.jetty.http.HttpStatus

object PurifierServerController : PurifierControllerBase() {
    private val serverLogic = PurifierServerLogic()
    private val userLogic = PurifierUserLogic()

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun health(ctx: Context) {
        ctx.json(this.successResponse())
    }

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(ServerInfoModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun info(ctx: Context) {
        val payload = serverLogic.serverInfo()
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(ServerSystemInfoModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun systemInfo(ctx: Context) {
        val payload = serverLogic.systemInfo()
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            requestBody = OpenApiRequestBody([OpenApiContent(RequestUserModel::class)]),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(status = HttpStatus.NOT_FOUND_404.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun addWhitelist(ctx: Context) {
        val req = ctx.bodyAsClass(RequestUserModel::class.java)
        val user = userLogic.fetchUser(req.username)
        if (user != null) {
            when (user) {
                is OfflinePlayer -> user.isWhitelisted = true
                is Player -> user.isWhitelisted = true
            }
            userLogic.minecraftServer.reloadWhitelist()
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            requestBody = OpenApiRequestBody([OpenApiContent(RequestUserModel::class)]),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(status = HttpStatus.NOT_FOUND_404.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun removeWhitelist(ctx: Context) {
        val req = ctx.bodyAsClass(RequestUserModel::class.java)
        val user = userLogic.fetchUser(req.username)
        if (user != null) {
            when (user) {
                is OfflinePlayer -> user.isWhitelisted = false
                is Player -> user.isWhitelisted = false
            }
            userLogic.minecraftServer.reloadWhitelist()
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            requestBody = OpenApiRequestBody([OpenApiContent(RequestUserModel::class)]),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(status = HttpStatus.NOT_FOUND_404.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun addOp(ctx: Context) {
        val req = ctx.bodyAsClass(RequestUserModel::class.java)
        val user = userLogic.fetchUser(req.username)
        if (user != null) {
            when (user) {
                is OfflinePlayer -> user.isOp = true
                is Player -> user.isOp = true
            }
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            requestBody = OpenApiRequestBody([OpenApiContent(RequestUserModel::class)]),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun removeOp(ctx: Context) {
        val req = ctx.bodyAsClass(RequestUserModel::class.java)
        val user = userLogic.fetchUser(req.username)
        if (user != null) {
            when (user) {
                is OfflinePlayer -> user.isOp = false
                is Player -> user.isOp = false
            }
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.NOT_FOUND_404)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            requestBody = OpenApiRequestBody([OpenApiContent(SetBroadcastModel::class)]),
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun broadcast(ctx: Context) {
        val req = ctx.bodyAsClass(SetBroadcastModel::class.java)
        val payload = serverLogic.broadcast(req.message)
        if (payload != null) {
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
                OpenApiResponse(
                        status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                )
            ]
    )
    fun save(ctx: Context) {
        val payload = serverLogic.save()
        if (payload != null) {
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString())
            ]
    )
    fun reload(ctx: Context) {
        serverLogic.reload()
        ctx.json(this.successResponse())
    }

    @OpenApi(
            responses = [
                OpenApiResponse(
                        status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)]
                ),
                OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
                OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString())
            ]
    )
    fun shutdown(ctx: Context) {
        serverLogic.shutdown()
        ctx.json(this.successResponse())
    }
}
