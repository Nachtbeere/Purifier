package net.nachtbeere.minecraft.purifier

class CommonResponseModel(val result: String) {
    override fun toString(): String {
        return ""
    }
}

class ServerInfoModel(val version: String,
                      val basedOn: String,
                      val motd: String,
                      val currentPlayers: Int,
                      val maxPlayers: Int,
                      val isOnlineMode: Boolean,
                      val isHardcore: Boolean) {
    override fun toString(): String {
        return ""
    }
}

class CurrentUsersModel(val total: Int,
                        val users: ArrayList<UserModel>) {
    override fun toString(): String {
        return ""
    }
}

class LocationModel(val world: String,
                    val x: Double,
                    val y: Double,
                    val z: Double) {
    override fun toString(): String {
        return ""
    }
}

class UserModel(val username: String,
                val locale: String,
                val level: Int,
                val exp: Float,
                val hunger: Int,
                val vital: Double,
                val location: LocationModel) {
    override fun toString(): String {
        return ""
    }
}