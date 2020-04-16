package net.nachtbeere.minecraft.purifier

import com.google.gson.Gson
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import net.nachtbeere.minecraft.purifier.routings.SwaggerServlet
import javax.servlet.http.HttpServletResponse

class PurifierServer(private val port: Int, private val maxThread: Int, val pluginInstance: Purifier)  {
    // TODO: load from config file
    private val server: Server

    init {
        val threadPool = QueuedThreadPool(maxThread)
        threadPool.isDaemon = true

        server = Server(threadPool)

        val connector = ServerConnector(server)
        connector.host = "0.0.0.0"
        connector.port = port
        server.addConnector(connector)

        val ctx = ServletContextHandler(ServletContextHandler.NO_SESSIONS)
        ctx.contextPath = "/"
        server.handler =  ctx
        val rootHolder = ctx.addServlet(DefaultServlet::class.java, "/api/*")
        rootHolder.initOrder = 0
        rootHolder.setInitParameter("jersey.config.server.provider.packages",
            "net.nachtbeere.minecraft.purifier"
        )
        val swaggerServlet = ServletHolder(SwaggerServlet())
        swaggerServlet.initOrder = 0
//        swaggerServlet.setInitParameter(
//                "com.sun.jersey.config.property.packages",
//                "com.api.resources;io.swagger.jaxrs.json;io.swagger.jaxrs.listing;" +
//                        "net.nachtbeere.minecraft.purifier"
//        )
        swaggerServlet.setInitParameter("swagger.api.basepath", "http://localhost:8080")
        ctx.addServlet(swaggerServlet, "/*")
        var initOrder = 1
        for ((endpoint, currentServlet) in servlets(pluginInstance)) {
            val apiServlet = ServletHolder(currentServlet)
            apiServlet.initOrder = initOrder
            ctx.addServlet(
                apiServlet,
                buildString {
                    append("/api/")
                    append(pluginInstance.apiVersion)
                    append("/")
                    append(endpoint)
                }
            )
            initOrder += 1
        }
//        val swaggerServlet = ServletHolder(DefaultJaxrsConfig())
//        swaggerServlet.setInitParameter("swagger.api.basepath", "/")
//        swaggerServlet.setInitParameter("api.version", "1.0.0")
//        swaggerServlet.initOrder = initOrder
//        servletContextHandler.addServlet(swaggerServlet, "/swagger-core")
    }

    fun start() {
        this.server.start()
    }

    fun stop() {
        this.server.stop()
    }
}

fun response(resp: HttpServletResponse, statusCode: Int, result: String?) {
    resp.status = statusCode
    resp.contentType = "application/json"
    resp.characterEncoding = "UTF-8"
    val out = resp.writer
    out.print(result)
    out.flush()
}

fun jsonMaker(payload: Any?): String {
    if (payload == null) {
        return "{}"
    }
    val gson = Gson()
    return gson.toJson(payload)
}

