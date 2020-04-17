package net.nachtbeere.minecraft.purifier.controllers

import io.javalin.http.Context
import net.nachtbeere.minecraft.purifier.*

object PurifierUsersController : PurifierControllerBase() {
    fun currentUsers(ctx: Context)  {
        this.log("Current User List Request Accepted.")
        val payload = futureTask {
            val currentUsers = bukkitServer.onlinePlayers
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
    }
}