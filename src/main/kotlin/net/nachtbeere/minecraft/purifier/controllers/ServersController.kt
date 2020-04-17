package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import net.nachtbeere.minecraft.purifier.*
import org.eclipse.jetty.http.HttpStatus

object PurifierServersController : PurifierControllerBase() {
    @OpenApi(
        responses = [OpenApiResponse(status = "200", content = [OpenApiContent(CommonResponseModel::class)])]
    )
    fun health(ctx: Context) {
        ctx.json(CommonResponseModel(result = "success"))
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = "200", content = [OpenApiContent(ServerInfoModel::class)]),
            OpenApiResponse(status = "500", content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun info(ctx: Context) {
        this.log("Server Info Request Accepted.")
        val payload =  this.futureTask {
            ServerInfoModel(
                    version = bukkitServer.version,
                    basedOn = bukkitServer.name,
                    motd = bukkitServer.motd,
                    currentPlayers = bukkitServer.onlinePlayers.size,
                    maxPlayers = bukkitServer.maxPlayers,
                    isOnlineMode = bukkitServer.onlineMode,
                    isHardcore = bukkitServer.isHardcore
            )
        }
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }

    fun addWhitelist(ctx: Context) {
//        bukkitServer.whi
    }

    fun removeWhitelist(ctx: Context) {

    }

    fun addOpList(ctx: Context) {
//        bukkitServer.whi
    }

    fun removeOpList(ctx: Context) {

    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = "200", content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(status = "500", content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun save(ctx: Context) {
        val payload = this.futureTask {
            bukkitServer.worlds.iterator().forEach { w ->
                this.log("Sending Save Request of ".plus(w.name))
                w.save()
                this.log("Save Complete.")
            }
            CommonResponseModel(result = "SUCCESS")
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
            OpenApiResponse(status = "200", content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun reload(ctx: Context) {
        this.log("Reload Request Accepted.")
        this.futureTaskLater(3) { bukkitServer.reload() }
        val payload = CommonResponseModel(result = "SUCCESS")
        ctx.json(payload)
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = "200", content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun shutdown(ctx: Context) {
        this.log("Shutdown Request Accepted.")
        bukkitServer.shutdown()
        val payload = CommonResponseModel(result = "SUCCESS")
        ctx.json(payload)
    }
}
