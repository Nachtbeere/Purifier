package net.nachtbeere.minecraft.purifier.logics

import com.sun.management.OperatingSystemMXBean
import net.nachtbeere.minecraft.purifier.models.ServerInfoModel
import net.nachtbeere.minecraft.purifier.models.ServerSystemInfoModel
import org.bukkit.ChatColor
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.text.DecimalFormat
import java.time.*
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
        return futureTask {
            val runtimeBean = ManagementFactory.getRuntimeMXBean()
            ServerSystemInfoModel(
                startsAt = serverStartTime(runtimeBean).format(DateTimeFormatter.ISO_DATE_TIME),
                uptime = uptimeParser(uptimeCalculator(runtimeBean)),
                processorLoads = "${DecimalFormat("#.#").format(processorLoadsCalculator())}%",
                memoryLoads = "${DecimalFormat("#.#").format(memoryLoadsCalculator())}%"
            )
        } as ServerSystemInfoModel?
    }

    private fun processorLoadsCalculator(): Double {
        val osBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean
        return osBean.systemLoadAverage
    }

    private fun memoryLoadsCalculator(): Double {
        val currentRuntime = Runtime.getRuntime()
        val maxHeapMemory = (currentRuntime.maxMemory() / (1024 * 1024))
        val allocatedHeapMemory = (currentRuntime.totalMemory() / (1024 * 1024))
        return ((allocatedHeapMemory.toDouble() / maxHeapMemory) * 100)
    }

    private fun serverStartTime(runtimeBean: RuntimeMXBean): OffsetDateTime {
        return OffsetDateTime.ofInstant(
            Instant.ofEpochMilli(runtimeBean.startTime),
            ZoneId.systemDefault()
        )
    }

    private fun serverCurrentTime(runtimeBean: RuntimeMXBean): OffsetDateTime {
        return OffsetDateTime.ofInstant(
            Instant.ofEpochMilli(runtimeBean.startTime + runtimeBean.uptime),
            ZoneId.systemDefault()
        )
    }

    private fun uptimeCalculator(runtimeBean: RuntimeMXBean): Duration {
        return Duration.between(serverStartTime(runtimeBean), serverCurrentTime(runtimeBean))
    }

    private fun uptimeParser(uptimeDuration: Duration): String {
        val hours = uptimeDuration.toHours()
        val minutes = uptimeDuration.minusHours(hours).toMinutes()
        val seconds = uptimeDuration.minusHours(hours).minusMinutes(minutes).toMillis() / 1000
        return ""
            .plus(if (hours < 10) "0".plus(hours) else hours)
            .plus(":")
            .plus(if (minutes < 10) "0".plus(minutes) else minutes)
            .plus(":")
            .plus(if (seconds < 10) "0".plus(seconds) else seconds)
    }
}