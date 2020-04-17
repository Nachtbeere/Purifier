package net.nachtbeere.minecraft.purifier

import com.google.gson.Gson
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.core.JavalinConfig
import io.javalin.http.Context
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

class PurifierServer(private val port: Int, val pluginInstance: Purifier)  {
    // TODO: load from config file
    private var app: Javalin = Javalin.create { config: JavalinConfig ->
        config.defaultContentType = "application/json"
        config.contextPath = "/"
        config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
    }
    private val instance: Purifier = pluginInstance
    private val classLoader = Thread.currentThread().contextClassLoader
    private val log = instance.logger

    private fun getOpenApiOptions(): OpenApiOptions {
        val license  = License()
        license.name = "AGPLv3"
        license.url = "https://raw.githubusercontent.com/Nachtbeere/Purifier/master/LICENSE"
        val applicationInfo: Info = Info()
            .title("Purifier")
            .version("1.0")
            .description("Purifier Bukkit API")
            .license(license)
        return OpenApiOptions(applicationInfo).path("/swagger.json")
    }

    init {
        Thread.currentThread().contextClassLoader = Purifier::class.java.classLoader

        app.before { ctx -> log.info(ctx.req.pathInfo) }

        app.get("/") { ctx: Context -> ctx.result("Purifier Bukkit API") }
    }

    fun start() {
        routingTable(app)
        app.start(port)
        Thread.currentThread().contextClassLoader = classLoader
    }

    fun stop() {
        app.stop()
    }
}
