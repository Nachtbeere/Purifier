package net.nachtbeere.minecraft.purifier.models

import java.time.LocalDate
import java.util.*

class AuthorizeUserModel(val username: String, val password: String)

class SetTimeModel(val moment: String)

class SetManualTimeModel(val time: Long)

class SetBroadcastModel(val message: String)

class SetUserGameModeModel(val gamemode: Int)

class RequestUserModel(val username: String)

class RequestBanModel(
    val username: String,
    val reason: String,
    val expiredAt: Date
)
