package net.nachtbeere.minecraft.purifier

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.security.SecurityUtil.roles
import net.nachtbeere.minecraft.purifier.controllers.*

fun route(app: Javalin) {
    app.routes {
        ApiBuilder.path("api") {
            ApiBuilder.path(Constants.apiVersion) {
                ApiBuilder.get("/health", PurifierServersController::health, roles(Permission.ANON))
                ApiBuilder.get("/info", PurifierServersController::info, roles(Permission.READ))
                ApiBuilder.get("/system-info", PurifierServersController::systemInfo, roles(Permission.READ))
                ApiBuilder.path("servers") {
                    ApiBuilder.post("/broadcast", PurifierServersController::broadcast, roles(Permission.ADMIN))
                    ApiBuilder.put("/save", PurifierServersController::save, roles(Permission.ADMIN))
                    ApiBuilder.put("/reload", PurifierServersController::reload, roles(Permission.ADMIN))
                    ApiBuilder.put("/shutdown", PurifierServersController::shutdown, roles(Permission.ADMIN))
//                ApiBuilder.post("/whitelist/:username", PurifierServersController::addWhitelist)
//                ApiBuilder.delete("/whitelist/:username", PurifierServersController::removeWhitelist)
//                ApiBuilder.post("/op/:username", PurifierServersController::addOpList)
//                ApiBuilder.delete("/op/:username", PurifierServersController::removeOpList)
                }
                ApiBuilder.path("worlds") {
                    ApiBuilder.get("/current-time", PurifierWorldsController::currentTime, roles(Permission.READ))
                    ApiBuilder.put("/:world/time", PurifierWorldsController::setTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/time/manual", PurifierWorldsController::setManualTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/storm", PurifierWorldsController::toggleStorm, roles(Permission.WRITE))
                }
                ApiBuilder.path("users") {
                    ApiBuilder.get(PurifierUsersController::onlineUsers, roles(Permission.READ)) // Default as online. maybe remove later.
                    ApiBuilder.get("/online", PurifierUsersController::onlineUsers, roles(Permission.READ))
                    ApiBuilder.get("/offline", PurifierUsersController::offlineUsers, roles(Permission.READ))
                    ApiBuilder.put("/:username/gamemode", PurifierUsersController::setUserGameMode, roles(Permission.WRITE))
                }
            }
        }
    }
}
