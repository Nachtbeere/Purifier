package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.CommonResponseModel
import net.nachtbeere.minecraft.purifier.Purifier
import net.nachtbeere.minecraft.purifier.jsonMaker

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
        } catch (e: Throwable) {
            this.severeLog(e.toString())
            null
        }
    }

    fun futureTaskLater(task: () -> Any) {
        this.instance.server.scheduler.runTaskLater(instance, Runnable { task() }, 60)
    }

    fun commonResponse(): String {
        val payload = this.futureTask {
            CommonResponseModel(
                    result = "SUCCESS"
            )
        }
        return jsonMaker(payload)
    }
}


