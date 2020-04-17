package net.nachtbeere.minecraft.purifier

import org.bukkit.plugin.java.JavaPlugin

class Purifier : JavaPlugin() {
    private val purifierServer = PurifierServer(port = 8080,
                                                pluginInstance = this)

    override fun onEnable() {
        this.purifierServer.start()
    }

    override fun onDisable() {
        this.purifierServer.stop()
    }
}
