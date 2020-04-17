package net.nachtbeere.minecraft.purifier

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import net.nachtbeere.minecraft.purifier.controllers.*

fun routingTable(app: Javalin) {
    app.routes {
        ApiBuilder.path(Constants.apiVersion) {
            ApiBuilder.get("/health", PurifierServersController::health)
            ApiBuilder.get("/info", PurifierServersController::info)
            ApiBuilder.path("servers") {
                ApiBuilder.put("/save", PurifierServersController::save)
                ApiBuilder.put("/reload", PurifierServersController::reload)
                ApiBuilder.put("/shutdown", PurifierServersController::shutdown)
            }
            ApiBuilder.path("games") {
                ApiBuilder.get("/time", PurifierGameController::currentTime)
            }
            ApiBuilder.path("users") {
                ApiBuilder.get(PurifierUsersController::currentUsers)
            }
        }
    }
}
