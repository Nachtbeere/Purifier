package net.nachtbeere.minecraft.purifier

import org.bukkit.configuration.MemorySection
import java.io.File
import org.bukkit.plugin.java.JavaPlugin

class Purifier : JavaPlugin() {
    private var purifierServer: PurifierServer? = null

    override fun onLoad() {
        if (!(File(this.dataFolder, "config.yml")).exists()) this.saveDefaultConfig()
        purifierServer = PurifierServer(port = this.config.getInt("port"),
                                        isDebug = this.config.getBoolean("debug"),
                                        auth = Auth(
                                            log = this.logger,
                                            config = this.config.get("authentication") as MemorySection),
                                        cors = Cors(
                                            log = this.logger,
                                            config = this.config.get("cors") as MemorySection),
                                        pluginInstance = this)
    }

    override fun onEnable() {
        this.purifierServer!!.start()
    }

    override fun onDisable() {
        this.purifierServer!!.stop()
    }
}
