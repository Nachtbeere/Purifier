package net.nachtbeere.minecraft.purifier

import io.swagger.annotations.*
import org.eclipse.jetty.http.HttpStatus
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletResponse
import net.nachtbeere.minecraft.purifier.routings.*

fun servlets(pluginInstance: Purifier): HashMap<String, HttpServlet> {
    val servlets : HashMap<String, HttpServlet> = HashMap<String, HttpServlet>()
    servlets["health"] = ServletHealth(pluginInstance)
    servlets["info"] = ServletServerInfo(pluginInstance)
    servlets["game/time"] = ServletGameTime(pluginInstance)
    servlets["server/save"] = ServletServerSave(pluginInstance)
    servlets["server/reload"] = ServletServerReload(pluginInstance)
    servlets["server/shutdown"] = ServletServerShutdown(pluginInstance)
    servlets["users"] = ServletUsers(pluginInstance)
    return servlets
}

fun makeResponse(resp: HttpServletResponse, result: String) {
    if (result == "{}") {
        response(resp, HttpStatus.Code.INTERNAL_SERVER_ERROR.code, result)
    } else {
        response(resp, HttpStatus.Code.OK.code, result)
    }
}