package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus
import com.sun.management.OperatingSystemMXBean
import io.javalin.plugin.openapi.annotations.OpenApiRequestBody
import org.bukkit.ChatColor
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter


object PurifierServersController : PurifierControllerBase() {
    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun health(ctx: Context) {
        ctx.json(this.successResponse())
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(ServerInfoModel::class)]),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun info(ctx: Context) {
        // TODO: Add Current TPS for all API(bukkit/spigot/paper)
        val payload =  this.futureTask {
            ServerInfoModel(
                    version = bukkitServer.version,
                    basedOn = bukkitServer.name,
                    motd = bukkitServer.motd,
                    tps = null,
                    gameMode = bukkitServer.defaultGameMode.name,
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
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
            responses = [
                OpenApiResponse(status = HttpStatus.OK_200.toString(),
                        content = [OpenApiContent(ServerSystemInfoModel::class)]),
                OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                        content = [OpenApiContent(CommonResponseModel::class)])
            ]
    )
    fun systemInfo(ctx: Context) {
        val payload =  this.futureTask {
            val runtimeBean = ManagementFactory.getRuntimeMXBean()
            val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
            val currentRuntime = Runtime.getRuntime()
            val startTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(runtimeBean.startTime),
                                                     ZoneId.systemDefault())
            val currentTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(runtimeBean.startTime + runtimeBean.uptime),
                                                       ZoneId.systemDefault())
            val timeDuration = Duration.between(startTime, currentTime)
            val systemLoadAverage = osBean.systemLoadAverage
            val maxHeapMemory = (currentRuntime.maxMemory()/(1024*1024))
            val allocatedHeapMemory = (currentRuntime.totalMemory()/(1024*1024))
            ServerSystemInfoModel(
                startsAt = startTime.format(DateTimeFormatter.ISO_DATE_TIME),
                uptime = OffsetDateTime.MIN.plus(timeDuration).format(DateTimeFormatter.ISO_LOCAL_TIME),
                processorLoads = "${DecimalFormat("#.#").format(systemLoadAverage)}%",
                memoryLoads = "${((allocatedHeapMemory.toDouble() / maxHeapMemory) * 100)}%"
            )
        }
        if (payload != null) {
            ctx.json(payload)
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
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
            requestBody = OpenApiRequestBody([OpenApiContent(SetBroadcastModel::class)]),
            responses = [
                OpenApiResponse(status = HttpStatus.OK_200.toString(),
                                content = [OpenApiContent(CommonResponseModel::class)]),
                OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                                content = [OpenApiContent(CommonResponseModel::class)])
            ]
    )
    fun broadcast(ctx: Context) {
        val req = ctx.bodyAsClass(SetBroadcastModel::class.java)
        val payload = this.futureTask {
            bukkitServer.broadcastMessage("${ChatColor.GRAY}${ChatColor.ITALIC}" +
                    "[${this.currentPlugin.config.getString("broadcast_prefix")}] " +
                    "${ChatColor.RESET}" + req.message)
        }
        if (payload != null) {
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)]),
            OpenApiResponse(status = HttpStatus.INTERNAL_SERVER_ERROR_500.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun save(ctx: Context) {
        val payload = this.futureTask {
            bukkitServer.worlds.iterator().forEach { w ->
                this.log("Sending Save Request of ".plus(w.name))
                w.save()
                this.log("Save Complete.")
            }
        }
        if (payload != null) {
            ctx.json(this.successResponse())
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500)
            ctx.json(this.failedResponse())
        }
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun reload(ctx: Context) {
        this.futureTaskLater(3) { bukkitServer.reload() }
        ctx.json(this.successResponse())
    }

    @OpenApi(
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                            content = [OpenApiContent(CommonResponseModel::class)])
        ]
    )
    fun shutdown(ctx: Context) {
        bukkitServer.shutdown()
        ctx.json(this.successResponse())
    }
}
