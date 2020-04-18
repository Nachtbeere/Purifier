package net.nachtbeere.minecraft.purifier

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import io.javalin.http.Context
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.json.FromJsonMapper
import io.javalin.plugin.json.ToJsonMapper
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

class PurifierServer(private val port: Int, val pluginInstance: Purifier)  {
    // TODO: load from config file
    private var gson: Gson = GsonBuilder().serializeNulls().create()
    private var app: Javalin = Javalin.create { config: JavalinConfig ->
        config.defaultContentType = "application/json"
        config.contextPath = "/"
        config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
        JavalinJson.fromJsonMapper = object : FromJsonMapper {
            override fun <T> map(json: String, targetClass: Class<T>) = gson.fromJson(json, targetClass)
        }

        JavalinJson.toJsonMapper = object : ToJsonMapper {
            override fun map(obj: Any): String = gson.toJson(obj)
        }
    }

    private val instance: Purifier = pluginInstance
    private val classLoader = Thread.currentThread().contextClassLoader
    private val log = instance.logger

    private fun getOpenApiOptions(): OpenApiOptions {
        val applicationInfo: Info = Info()
            .title("Purifier")
            .version("1.0")
            .description("Purifier Bukkit API")
            .license(License().apply {
                name="AGPLv3"
                url="https://raw.githubusercontent.com/Nachtbeere/Purifier/master/LICENSE"
            })
        return OpenApiOptions(applicationInfo).path("/api-docs")
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
