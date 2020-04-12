package net.nachtbeere.minecraft.purifier

import org.eclipse.jetty.http.HttpStatus
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

fun servlets(pluginInstance: Purifier): HashMap<String, HttpServlet> {
    val servlets : HashMap<String, HttpServlet> = HashMap<String, HttpServlet>()
    servlets["health"] = ServletHealth(pluginInstance)
    servlets["info"] = ServletServerInfo(pluginInstance)
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

class ServletHealth(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).commonResponse()
        makeResponse(resp, result)
    }
}

class ServletServerInfo(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).info()
        makeResponse(resp, result)
    }
}

class ServletServerSave(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).save()
        makeResponse(resp, result)
    }
}

class ServletServerReload(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).reload()
        makeResponse(resp, result)
    }
}

class ServletServerShutdown(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).shutdown()
        makeResponse(resp, result)
    }
}

class ServletUsers(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierUsersController(pluginInstance).currentUsers()
        makeResponse(resp, result)
    }
}
