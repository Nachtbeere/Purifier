package net.nachtbeere.minecraft.purifier

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.SecurityUtil.roles
import net.nachtbeere.minecraft.purifier.controllers.*

fun route(app: Javalin) {
    app.routes {
        ApiBuilder.path("api") {
            ApiBuilder.path(Constants.apiVersion) {
                ApiBuilder.get("/health", PurifierServerController::health, roles(Permission.ANON))
                ApiBuilder.get("/info", PurifierServerController::info, roles(Permission.READ))
                ApiBuilder.get("/system-info", PurifierServerController::systemInfo, roles(Permission.READ))
                ApiBuilder.path("servers") {
                    ApiBuilder.post("/broadcast", PurifierServerController::broadcast, roles(Permission.ADMIN))
                    ApiBuilder.put("/save", PurifierServerController::save, roles(Permission.ADMIN))
                    ApiBuilder.put("/reload", PurifierServerController::reload, roles(Permission.ADMIN))
                    ApiBuilder.put("/shutdown", PurifierServerController::shutdown, roles(Permission.ADMIN))
//                ApiBuilder.post("/whitelist/:username", PurifierServerController::addWhitelist)
//                ApiBuilder.delete("/whitelist/:username", PurifierServerController::removeWhitelist)
//                ApiBuilder.post("/op/:username", PurifierServerController::addOpList)
//                ApiBuilder.delete("/op/:username", PurifierServerController::removeOpList)
                }
                ApiBuilder.path("worlds") {
//                    ApiBuilder.get(PurifierWorldsController::worlds, roles(Permission.READ))
                    ApiBuilder.get("/current-time", PurifierWorldController::currentTime, roles(Permission.READ))
                    ApiBuilder.put("/:world/time", PurifierWorldController::setTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/time/manual", PurifierWorldController::setManualTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/storm", PurifierWorldController::toggleStorm, roles(Permission.WRITE))
                }
                ApiBuilder.path("users") {
                    ApiBuilder.get(PurifierUserController::onlineUsers, roles(Permission.READ)) // Default as online. maybe remove later.
                    ApiBuilder.get("/online", PurifierUserController::onlineUsers, roles(Permission.READ))
                    ApiBuilder.get("/offline", PurifierUserController::offlineUsers, roles(Permission.READ))
                    ApiBuilder.put("/:username/gamemode", PurifierUserController::setUserGameMode, roles(Permission.WRITE))
                }
            }
        }
    }
}
