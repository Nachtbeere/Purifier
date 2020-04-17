package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*
import net.nachtbeere.minecraft.purifier.*
import java.util.logging.Level

object PurifierServersController : PurifierControllerBase() {
    @OpenApi(
        responses = [OpenApiResponse(status = "200", content = [OpenApiContent(CommonResponseModel::class)])]
    )
    fun health(ctx: Context) {
        ctx.json(CommonResponseModel(result = "success"))
    }

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
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }

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
        }
    }

    fun reload(ctx: Context) {
        this.log("Reload Request Accepted.")
        this.futureTaskLater { bukkitServer.reload() }
        val payload = CommonResponseModel(result = "SUCCESS")
        ctx.json(payload)
    }

    fun shutdown(ctx: Context) {
        this.log("Shutdown Request Accepted.")
        bukkitServer.shutdown()
        val payload = CommonResponseModel(result = "SUCCESS")
        ctx.json(payload)
    }
}
