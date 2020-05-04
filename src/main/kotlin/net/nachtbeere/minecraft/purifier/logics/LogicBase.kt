package net.nachtbeere.minecraft.purifier.logics

import net.nachtbeere.minecraft.purifier.Constants
import net.nachtbeere.minecraft.purifier.Purifier
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin

open class PurifierLogicBase() {
    var minecraftServer: Server = bukkitServer()
    var currentPlugin: Purifier = currentPlugin() as Purifier

    private fun bukkitServer(): Server = Bukkit.getServer()

    private fun currentPlugin(): Plugin = minecraftServer.pluginManager.getPlugin(Constants.packageName) as Plugin

    fun log(msg: String) = minecraftServer.logger.info(msg)

    fun warnLog(msg: String) = minecraftServer.logger.warning(msg)

    fun severeLog(msg: String) = minecraftServer.logger.severe(msg)

    fun futureTask(task: () -> Any): Any? {
        val future = this.minecraftServer.scheduler.callSyncMethod(this.currentPlugin) { task() }
        return try {
            future.get()
        } catch (e: Throwable) {
            this.severeLog(e.toString())
            null
        }
    }

    fun futureTaskLater(seconds: Long, task: () -> Any) {
        this.minecraftServer.scheduler.runTaskLater(this.currentPlugin, Runnable { task() }, (20 * seconds))
    }

    fun futureAsyncTask(task: () -> Any): Any? {
        val async = this.minecraftServer.scheduler.runTaskAsynchronously(this.currentPlugin, Runnable { task() })
        return try {
            async
        } catch (e: Throwable) {
            this.severeLog(e.toString())
            null
        }
    }
}