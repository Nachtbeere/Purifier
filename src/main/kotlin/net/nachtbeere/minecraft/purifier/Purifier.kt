package net.nachtbeere.minecraft.purifier

import java.io.File
import org.bukkit.plugin.java.JavaPlugin

class Purifier : JavaPlugin() {
    private var purifierServer: PurifierServer? = null

    override fun onLoad() {
        if (!(File(this.dataFolder, "config.yml")).exists()) this.saveDefaultConfig()
        purifierServer = PurifierServer(port = this.config.getInt("port"),
                                        isDebug = this.config.getBoolean("debug"),
                                        pluginInstance = this)
    }

    override fun onEnable() {
        this.purifierServer!!.start()
    }

    override fun onDisable() {
        this.purifierServer!!.stop()
    }
}
