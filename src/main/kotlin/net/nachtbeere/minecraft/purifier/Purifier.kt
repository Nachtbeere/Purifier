package net.nachtbeere.minecraft.purifier

import org.bukkit.plugin.java.JavaPlugin

class Purifier : JavaPlugin() {
    public val apiVersion = "v1"
    private val purifierServer = PurifierServer(port = 8080,
                                                maxThread = 8,
                                                pluginInstance = this)

    override fun onEnable() {
        this.purifierServer.start()
    }

    override fun onDisable() {
        this.purifierServer.stop()
    }

    fun log(msg: String) {
        this.logger.info(msg)
    }
}
