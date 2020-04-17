package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.Constants
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin

open class PurifierControllerBase() {
    var bukkitServer: Server = bukkitServer()
    var currentPlugin: Plugin = currentPlugin()

    private fun bukkitServer(): Server {
        return Bukkit.getServer()
    }

    private fun currentPlugin(): Plugin {
        return bukkitServer.pluginManager.getPlugin(Constants.packageName) as Plugin
    }

    fun log(msg: String) {
        bukkitServer.logger.info(msg)
    }

    fun warnLog(msg: String) {
        bukkitServer.logger.warning(msg)
    }

    fun severeLog(msg: String) {
        bukkitServer.logger.severe(msg)
    }

    fun futureTask(task: () -> Any): Any? {
        val future = this.bukkitServer.scheduler.callSyncMethod(this.currentPlugin) { task() }
        return try {
            future.get()
        } catch (e: Throwable) {
            this.severeLog(e.toString())
            null
        }
    }

    fun futureTaskLater(seconds: Long, task: () -> Any) {
        this.bukkitServer.scheduler.runTaskLater(this.currentPlugin, Runnable { task() }, (20 * seconds))
    }
}


