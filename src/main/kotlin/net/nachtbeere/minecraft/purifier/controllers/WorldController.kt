package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import net.nachtbeere.minecraft.purifier.logics.PurifierWorldLogic
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus

object PurifierWorldController : PurifierControllerBase() {
    private val worldLogic = PurifierWorldLogic()

    @OpenApi(
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(GameWorldsModel::class)]
            ),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            )
        ]
    )
    fun currentTime(ctx: Context) {
        val payload = worldLogic.currentTimes()
        if (payload != null) {
            ctx.json(GameWorldsModel(worlds = payload))
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetTimeModel::class)]
        ),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            ),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            )
        ]
    )
    fun setTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val req = ctx.bodyAsClass(SetTimeModel::class.java)
        val statusCode = worldLogic.setTime(paramWorld, req.moment)
        if (statusCode != null && statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
        ctx.status(statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR_500)
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(
            content = [OpenApiContent(SetManualTimeModel::class)]
        ),
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            ),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            )
        ]
    )
    fun setManualTime(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val req = ctx.bodyAsClass(SetManualTimeModel::class.java)
        val statusCode = worldLogic.setManualTime(paramWorld, req.time)
        if (statusCode != null && statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
        ctx.status(statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR_500)
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
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            )
        ]
    )
    fun toggleStorm(ctx: Context) {
        val paramWorld = ctx.pathParam(":world")
        val statusCode = worldLogic.toggleStorm(paramWorld)
        if (statusCode != null && statusCode != HttpStatus.OK_200) {
            ctx.json(failedResponse())
        } else {
            ctx.json(successResponse())
        }
        ctx.status(statusCode ?: HttpStatus.INTERNAL_SERVER_ERROR_500)
    }

    @OpenApi(
        responses = [
            OpenApiResponse(
                status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(GameWorldsModel::class)]
            ),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString()),
            OpenApiResponse(status = HttpStatus.FORBIDDEN_403.toString()),
            OpenApiResponse(
                status = HttpStatus.NOT_FOUND_404.toString(),
                content = [OpenApiContent(CommonResponseModel::class)]
            )
        ]
    )
    fun worlds(ctx: Context) {
        val statusCode = worldLogic.worlds()
        ctx.json(failedResponse())
    }
}
