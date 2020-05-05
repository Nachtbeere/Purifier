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
//                    ApiBuilder.post("/whitelist", PurifierServerController::addWhitelist, roles(Permission.ADMIN))
//                    ApiBuilder.delete("/whitelist", PurifierServerController::removeWhitelist, roles(Permission.ADMIN))
                    ApiBuilder.post("/op", PurifierServerController::addOp, roles(Permission.ADMIN))
                    ApiBuilder.delete("/op", PurifierServerController::removeOp, roles(Permission.ADMIN))
                }
                ApiBuilder.path("worlds") {
//                    ApiBuilder.get(PurifierWorldsController::worlds, roles(Permission.READ))
                    ApiBuilder.get("/current-time", PurifierWorldController::currentTime, roles(Permission.READ))
                    ApiBuilder.get("/:world/time", PurifierWorldController::time, roles(Permission.READ))
                    ApiBuilder.put("/:world/time", PurifierWorldController::setTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/time/manual", PurifierWorldController::setManualTime, roles(Permission.WRITE))
                    ApiBuilder.put("/:world/storm", PurifierWorldController::toggleStorm, roles(Permission.WRITE))
                }
                ApiBuilder.path("users") {
                    ApiBuilder.get(PurifierUserController::users, roles(Permission.READ))
                    ApiBuilder.get("/:username", PurifierUserController::user, roles(Permission.READ))
                    ApiBuilder.put("/:username/gamemode", PurifierUserController::setUserGameMode, roles(Permission.WRITE))
                    ApiBuilder.get("/:uuid", PurifierUserController::user, roles(Permission.READ))
                }
                ApiBuilder.path("mojang-users") {
                    ApiBuilder.get("/:username", PurifierUserController::mojang_user, roles(Permission.READ))
                }
            }
        }
    }
}
