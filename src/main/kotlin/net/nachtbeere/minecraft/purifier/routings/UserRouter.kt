package net.nachtbeere.minecraft.purifier.routings

import net.nachtbeere.minecraft.purifier.Purifier
import net.nachtbeere.minecraft.purifier.controllers.PurifierUsersController
import net.nachtbeere.minecraft.purifier.makeResponse
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class ServletUsers(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierUsersController(pluginInstance).currentUsers()
        makeResponse(resp, result)
    }
}