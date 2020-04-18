package net.nachtbeere.minecraft.purifier

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import net.nachtbeere.minecraft.purifier.controllers.*

fun routingTable(app: Javalin) {
    app.routes {
        ApiBuilder.path("api") {
            ApiBuilder.path(Constants.apiVersion) {
                ApiBuilder.get("/health", PurifierServersController::health)
                ApiBuilder.get("/info", PurifierServersController::info)
                ApiBuilder.get("/system-info", PurifierServersController::systemInfo)
                ApiBuilder.path("servers") {
                    ApiBuilder.put("/save", PurifierServersController::save)
                    ApiBuilder.put("/reload", PurifierServersController::reload)
                    ApiBuilder.put("/shutdown", PurifierServersController::shutdown)
//                ApiBuilder.post("/whitelist/:username", PurifierServersController::addWhitelist)
//                ApiBuilder.delete("/whitelist/:username", PurifierServersController::removeWhitelist)
//                ApiBuilder.post("/op/:username", PurifierServersController::addOpList)
//                ApiBuilder.delete("/op/:username", PurifierServersController::removeOpList)
                }
                ApiBuilder.path("worlds") {
                    ApiBuilder.get("/current-time", PurifierWorldsController::currentTime)
                    ApiBuilder.put("/:world/time", PurifierWorldsController::setTime)
                    ApiBuilder.put("/:world/time/manual", PurifierWorldsController::setManualTime)
                    ApiBuilder.put("/:world/storm", PurifierWorldsController::toggleStorm)
                }
                ApiBuilder.path("users") {
                    ApiBuilder.get("/online", PurifierUsersController::onlineUsers)
                    ApiBuilder.get("/offline", PurifierUsersController::offlineUsers)
                }
            }
        }
    }
}
