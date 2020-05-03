package net.nachtbeere.minecraft.purifier.models

class AuthorizeUserModel(val username: String, val password: String)

class SetTimeModel(val moment: String)

class SetManualTimeModel(val time: Long)

class SetBroadcastModel(val message: String)
