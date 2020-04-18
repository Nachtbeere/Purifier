package net.nachtbeere.minecraft.purifier.models

class GameTimeModel(val time: Long,
                    val age: Long) {
}

class LocationModel(val world: String,
                    val x: Double,
                    val y: Double,
                    val z: Double) {
}

class UserModel(val username: String,
                val locale: String,
                val level: Int,
                val exp: Float,
                val hunger: Int,
                val vital: Double,
                val location: LocationModel) {
}