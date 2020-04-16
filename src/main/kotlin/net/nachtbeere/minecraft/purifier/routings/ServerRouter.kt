package net.nachtbeere.minecraft.purifier.routings

import io.swagger.annotations.Api
import io.swagger.jaxrs.config.BeanConfig
import net.nachtbeere.minecraft.purifier.Purifier
import net.nachtbeere.minecraft.purifier.controllers.PurifierServerController
import net.nachtbeere.minecraft.purifier.makeResponse
import javax.servlet.ServletConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(name = "SwaggerConfig")
class SwaggerServlet(): HttpServlet() {
    override fun init(config: ServletConfig?) {
        super.init(config)
        val beanConfig = BeanConfig()
        beanConfig.version = "1.0.0"
        beanConfig.schemes = arrayOf("http")
        beanConfig.host = "localhost:8080"
        beanConfig.basePath = ("/api")
        beanConfig.resourcePackage = "io.swagger.resources"
        beanConfig.scan = true
        beanConfig.prettyPrint = true
        beanConfig.scannerId = "v1"
    }
}

@WebServlet(name = "ServerHealth")
class ServletHealth(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).commonResponse()
        makeResponse(resp, result)
    }
}

@Api(value = "/server/info", description = "get server info")
class ServletServerInfo(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).info()
        makeResponse(resp, result)
    }
}

@Api(value = "/server/save", description = "save current worlds")
class ServletServerSave(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).save()
        makeResponse(resp, result)
    }
}

@Api(value = "/server/reload", description = "reload server")
class ServletServerReload(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).reload()
        makeResponse(resp, result)
    }
}

@Api(value = "/server/reload", description = "shutdown server")
class ServletServerShutdown(private val pluginInstance: Purifier) : HttpServlet() {
    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val result = PurifierServerController(pluginInstance).shutdown()
        makeResponse(resp, result)
    }
}