package net.nachtbeere.minecraft.purifier.logics

import com.sun.management.OperatingSystemMXBean
import net.nachtbeere.minecraft.purifier.models.ServerInfoModel
import net.nachtbeere.minecraft.purifier.models.ServerSystemInfoModel
import org.bukkit.ChatColor
import java.lang.management.ManagementFactory
import java.text.DecimalFormat
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PurifierServerLogic : PurifierLogicBase() {
    fun save(): Any? {
        return this.futureTask {
            minecraftServer.worlds.iterator().forEach { w ->
                this.log("Sending Save Request of ".plus(w.name))
                w.save()
                this.log("Save Complete.")
            }
        }
    }

    fun reload() {
        this.futureTaskLater(3) { minecraftServer.reload() }
    }

    fun shutdown() {
        minecraftServer.shutdown()
    }

    fun broadcast(message: String): Any? {
        return this.futureTask {
            minecraftServer.broadcastMessage(
                    "${ChatColor.GRAY}${ChatColor.ITALIC}" +
                            "[${this.currentPlugin.config.getString("broadcast_prefix")}] " +
                            "${ChatColor.RESET}" + message
            )
        }
    }

    fun serverInfo(): Any? {
        // TODO: Add Current TPS for all API(bukkit/spigot/paper)
        return this.futureTask {
            ServerInfoModel(
                    version = minecraftServer.version,
                    basedOn = minecraftServer.name,
                    motd = minecraftServer.motd,
                    tps = null,
                    gameMode = minecraftServer.defaultGameMode.name,
                    currentPlayers = minecraftServer.onlinePlayers.size,
                    maxPlayers = minecraftServer.maxPlayers,
                    isOnlineMode = minecraftServer.onlineMode,
                    isHardcore = minecraftServer.isHardcore
            )
        }
    }

    fun systemInfo(): ServerSystemInfoModel? {
        val result: ServerSystemInfoModel? = this.futureTask {
            val runtimeBean = ManagementFactory.getRuntimeMXBean()
            val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
            val currentRuntime = Runtime.getRuntime()
            val startTime = OffsetDateTime.ofInstant(
                    Instant.ofEpochMilli(runtimeBean.startTime),
                    ZoneId.systemDefault()
            )
            val currentTime = OffsetDateTime.ofInstant(
                    Instant.ofEpochMilli(runtimeBean.startTime + runtimeBean.uptime),
                    ZoneId.systemDefault()
            )
            val timeDuration = Duration.between(startTime, currentTime)
            val systemLoadAverage = osBean.systemLoadAverage
            val maxHeapMemory = (currentRuntime.maxMemory() / (1024 * 1024))
            val allocatedHeapMemory = (currentRuntime.totalMemory() / (1024 * 1024))
            ServerSystemInfoModel(
                    startsAt = startTime.format(DateTimeFormatter.ISO_DATE_TIME),
                    uptime = OffsetDateTime.MIN.plus(timeDuration).format(DateTimeFormatter.ISO_LOCAL_TIME),
                    processorLoads = "${DecimalFormat("#.#").format(systemLoadAverage)}%",
                    memoryLoads = "${((allocatedHeapMemory.toDouble() / maxHeapMemory) * 100)}%"
            )
        } as ServerSystemInfoModel?
        return result
    }
}