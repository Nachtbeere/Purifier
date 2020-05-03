package net.nachtbeere.minecraft.purifier.models

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

open class BaseResponseModel() {
    val timestamp = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
}

class CommonResponseModel(val result: String) : BaseResponseModel()

class TokenResponseModel(val token: String?) : BaseResponseModel()

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
) : BaseResponseModel()

class ServerSystemInfoModel(
    val startsAt: String,
    val uptime: String,
    val processorLoads: String,
    val memoryLoads: String
) : BaseResponseModel()

class UsersModel(
    val total: Int,
    val users: ArrayList<UserModel>
) : BaseResponseModel()

class SingleUserModel(val user: UserModel) : BaseResponseModel()

class CurrentTimeModel(
    val worldName: String,
    val currentTime: GameTimeModel
) : BaseResponseModel()

class GameWorldsModel(val worlds: ArrayList<*>) : BaseResponseModel()