package net.nachtbeere.minecraft.purifier

import java.util.concurrent.ExecutionException

open class PurifierControllerBase(private val instance: Purifier) {
    fun log(msg: String) {
        instance.logger.info(msg)
    }

    fun warnLog(msg: String) {
        instance.logger.warning(msg)
    }

    fun severeLog(msg: String) {
        instance.logger.severe(msg)
    }

    fun futureTask(task: () -> Any): Any? {
        val future = this.instance.server.scheduler.callSyncMethod(instance) { task() }
        return try {
            future.get()
        } catch(e: Throwable) {
            this.severeLog(e.toString())
            null
        }
    }

    fun futureTaskLater(task: () -> Any) {
        this.instance.server.scheduler.runTaskLater(instance, Runnable { task() }, 60)
    }
}

class PurifierServerController(private val instance: Purifier) : PurifierControllerBase(instance) {
    fun commonResponse(): String {
        val payload = this.futureTask {
            CommonResponseModel(
                result = "SUCCESS"
            )
        }
        return jsonMaker(payload)
    }

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

class PurifierUsersController(private val instance: Purifier) : PurifierControllerBase(instance) {
    fun currentUsers(): String {
        this.log("Current User List Request Accepted.")
        val payload = futureTask {
            val currentUsers = instance.server.onlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                    UserModel(
                        username = p.displayName,
                        locale = p.locale,
                        level = p.level,
                        exp = p.exp,
                        hunger = p.foodLevel,
                        vital = p.health,
                        location = LocationModel(
                            world = p.location.world!!.name,
                            x = p.location.x,
                            y = p.location.y,
                            z = p.location.z
                        )
                    )
                )
            }
            CurrentUsersModel(
                total=currentUsers.size,
                users=users
            )
        }
        return jsonMaker(payload)
    }
}