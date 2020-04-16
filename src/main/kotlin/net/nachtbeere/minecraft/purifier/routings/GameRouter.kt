package net.nachtbeere.minecraft.purifier.routings

import net.nachtbeere.minecraft.purifier.Purifier
import net.nachtbeere.minecraft.purifier.controllers.PurifierGameController
import net.nachtbeere.minecraft.purifier.makeResponse
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ServletGameTime(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierGameController(pluginInstance).currentTime()
        makeResponse(resp, result)
    }

    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        // TODO: GET Json payload
        req.reader
        val result = PurifierGameController(pluginInstance).setTime()
        makeResponse(resp, result)
    }
}