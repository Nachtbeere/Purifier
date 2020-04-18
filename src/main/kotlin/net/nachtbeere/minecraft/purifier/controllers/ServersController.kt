package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.OpenApi
import io.javalin.plugin.openapi.annotations.OpenApiContent
import io.javalin.plugin.openapi.annotations.OpenApiResponse
import org.eclipse.jetty.http.HttpStatus
import com.sun.management.OperatingSystemMXBean
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.time.*
import java.time.format.DateTimeFormatter


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
            ctx.json(CommonResponseModel(result = "FAILED"))
        }
    }

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
        bukkitServer.shutdown()
        val payload = CommonResponseModel(result = "SUCCESS")
        ctx.json(payload)
    }
}
