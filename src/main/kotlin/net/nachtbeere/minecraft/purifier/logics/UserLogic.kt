package net.nachtbeere.minecraft.purifier.logics

import net.nachtbeere.minecraft.purifier.PurifierGameMode
import net.nachtbeere.minecraft.purifier.models.LocationModel
import net.nachtbeere.minecraft.purifier.models.SingleUserModel
import net.nachtbeere.minecraft.purifier.models.UserModel
import net.nachtbeere.minecraft.purifier.models.UsersModel
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

class PurifierUserLogic : PurifierLogicBase() {
    private fun fetchUser(username: String): Any {
        val offlinePlayer = fetchOfflineUser(username)
        return if (offlinePlayer.isOnline) {
            fetchOnlineUser(offlinePlayer.uniqueId)!!
        } else {
            offlinePlayer
        }
    }

    private fun fetchOfflineUser(username: String): OfflinePlayer {
        val cachedUser = currentPlugin.userCache.user(username)
        return if (cachedUser != null) {
            val offlineUser = minecraftServer.getOfflinePlayer(cachedUser.uuid)
            if (offlineUser.hasPlayedBefore()) {
                offlineUser
            } else {
                fetchOfflineUserFromOutside(username)
            }
        } else {
            fetchOfflineUserFromOutside(username)
        }
    }

    private fun fetchOfflineUserFromOutside(username: String): OfflinePlayer {
        /*
        * Deprecated method. but only choice. use for users not in cache only.
        */
        return minecraftServer.getOfflinePlayer(username)
    }

    private fun fetchOnlineUser(username: String): Player? {
        return minecraftServer.getPlayer(fetchOfflineUser(username).uniqueId)
    }

    private fun fetchOnlineUser(uuid: UUID): Player? {
        return minecraftServer.getPlayer(uuid)
    }

    private fun userWrapper(player: Any?): UserModel {
        return when (player) {
            is Player -> {
                UserModel(
                        username = player.displayName,
                        locale = player.locale,
                        gamemode = player.gameMode.name,
                        level = player.level,
                        exp = player.exp,
                        hunger = player.foodLevel,
                        vital = player.health,
                        location = LocationModel(
                                world = player.location.world!!.name,
                                x = player.location.x,
                                y = player.location.y,
                                z = player.location.z
                        ),
                        online = player.isOnline
                )
            }
            is OfflinePlayer -> {
                log(player.name ?: "null")
                UserModel(
                        username = player.name ?: "",
                        locale = "",
                        gamemode = "",
                        level = 0,
                        exp = 0.toFloat(),
                        hunger = 0,
                        vital = 0.toDouble(),
                        location = LocationModel(
                                world = "",
                                x = 0.toDouble(),
                                y = 0.toDouble(),
                                z = 0.toDouble()
                        ),
                        online = player.isOnline
                )
            }
            else -> {
                UserModel(
                        username = "",
                        locale = "",
                        gamemode = "",
                        level = 0,
                        exp = 0.toFloat(),
                        hunger = 0,
                        vital = 0.toDouble(),
                        location = LocationModel(
                                world = "",
                                x = 0.toDouble(),
                                y = 0.toDouble(),
                                z = 0.toDouble()
                        ),
                        online = false
                )
            }
        }
    }

    fun user(username: String): Any? {
        return futureTask {
            val user = fetchUser(username)
            SingleUserModel(
                    user = userWrapper(user)
            )
        }
    }

    fun onlineUsers(): UsersModel? {
        return futureTask {
            val currentUsers = minecraftServer.onlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                users.add(
                        userWrapper(p)
                )
            }
            UsersModel(
                    total = currentUsers.size,
                    users = users
            )
        } as UsersModel?
    }

    fun offlineUsers(): UsersModel? {
        return futureTask {
            val currentUsers = minecraftServer.offlinePlayers
            val users = arrayListOf<UserModel>()
            currentUsers.iterator().forEach { p ->
                minecraftServer
                users.add(
                        userWrapper(p)
                )
            }
            UsersModel(
                    total = currentUsers.size,
                    users = users
            )
        } as UsersModel?
    }

    fun setUserGameMode(username: String, gamemode: Int): UserModel {
        return futureTask {
            val currentUser = minecraftServer.getPlayer(username)
            if (currentUser != null) {
                currentUser.gameMode = GameMode.valueOf(PurifierGameMode.valueOf(gamemode).toString())
            }
            userWrapper(currentUser)
        } as UserModel
    }
}