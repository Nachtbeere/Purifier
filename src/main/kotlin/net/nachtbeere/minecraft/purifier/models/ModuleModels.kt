package net.nachtbeere.minecraft.purifier.models


class GameTimeModel(val time: Long, val age: Long)

class LocationModel(val world: String, val x: Double, val y: Double, val z: Double)

class UserModel(
    val username: String,
    val locale: String,
    val gamemode: String,
    val level: Int,
    val exp: Float,
    val hunger: Int,
    val vital: Double,
    val location: LocationModel
)

class WorldModel(val name: String)

data class AuthUser(val name: String, val password: String, val roles: List<String>) {
    object ModelMapper {
        fun from(map: LinkedHashMap<String, HashMap<String, Any>>): AuthUser {
            val key = map.keys.iterator().next()
            return AuthUser(
                name = key,
                password = (map[key]?.get("password") ?: error("")) as String,
                roles = (map[key]?.get("roles") ?: error(listOf("ANON"))) as List<String>
            )
        }
    }
}
