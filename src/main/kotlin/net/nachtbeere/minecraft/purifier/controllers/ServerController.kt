package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.*

class PurifierServerController(private val instance: Purifier) : PurifierControllerBase(instance) {
    fun info(): String {
        this.log("Server Info Request Accepted.")
        val payload =  this.futureTask {
            ServerInfoModel(
                    version = this.instance.server.version,
                    basedOn = this.instance.server.name,
                    motd = this.instance.server.motd,
                    currentPlayers = this.instance.server.onlinePlayers.size,
                    maxPlayers = this.instance.server.maxPlayers,
                    isOnlineMode = this.instance.server.onlineMode,
                    isHardcore = this.instance.server.isHardcore
            )
        }
        return jsonMaker(payload)
    }

    fun save(): String {
        // TODO: Async
        this.futureTask {
            this.instance.server.worlds.iterator().forEach { w ->
                this.log("Sending Save Request of ".plus(w.name))
                w.save()
                this.log("Save Complete.")
            }
        }
        val payload = CommonResponseModel(result = "SUCCESS")
        return jsonMaker(payload)
    }

    fun reload(): String {
        this.log("Reload Request Accepted.")
        this.futureTaskLater { this.instance.server.reload() }
        val payload = CommonResponseModel(result = "SUCCESS")
        return jsonMaker(payload)
    }

    fun shutdown(): String {
        this.log("Shutdown Request Accepted.")
        this.instance.server.shutdown()
        val payload = CommonResponseModel(result = "SUCCESS")
        return jsonMaker(payload)
    }
}
