package net.nachtbeere.minecraft.purifier

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.util.thread.QueuedThreadPool
import javax.servlet.http.HttpServletResponse
import com.google.gson.Gson

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

        val servletContextHandler = ServletContextHandler()
        servletContextHandler.contextPath = "/api"
        server.handler = HandlerList(servletContextHandler)
        for ((endpoint, currentServlet) in servlets(pluginInstance)) {
            servletContextHandler.addServlet(
                ServletHolder(currentServlet),
                buildString {
                    append("/")
                    append(pluginInstance.apiVersion)
                    append("/")
                    append(endpoint)
                }
            )
        }
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

