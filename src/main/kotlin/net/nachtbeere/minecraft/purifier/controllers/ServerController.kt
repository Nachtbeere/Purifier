package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import net.nachtbeere.minecraft.purifier.logics.PurifierServerLogic
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import org.eclipse.jetty.http.HttpStatus

object PurifierServerController : PurifierControllerBase() {
    private val serverLogic = PurifierServerLogic()

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

    fun addWhitelist(ctx: Context) {

    }

    fun removeWhitelist(ctx: Context) {

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
    fun addOp(ctx: Context) {
        val req = ctx.bodyAsClass(RequestUserModel::class.java)
        // 대충 유저네임에서 유저 오브젝트 만드는 코드
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
