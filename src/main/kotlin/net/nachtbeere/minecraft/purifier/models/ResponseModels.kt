package net.nachtbeere.minecraft.purifier.models

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class BaseResponseModel() {
    val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
}

class CommonResponseModel(val result: String): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}

class TokenResponseModel(val token: String?): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}

class ServerInfoModel(
    val version: String,
    val basedOn: String,
    val motd: String,
    val tps: Int?,
    val gameMode: String,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val isOnlineMode: Boolean,
    val isHardcore: Boolean
): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}

class ServerSystemInfoModel(
    val startsAt: String,
    val uptime: String,
    val processorLoads: String,
    val memoryLoads: String
): BaseResponseModel()

class UsersModel(val total: Int,
                 val users: ArrayList<UserModel>): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}

class CurrentTimeModel(val worldName: String,
                       val currentTime: GameTimeModel): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}

class GameWorldsModel(val worlds: ArrayList<*>): BaseResponseModel() {
    override fun toString(): String {
        return ""
    }
}