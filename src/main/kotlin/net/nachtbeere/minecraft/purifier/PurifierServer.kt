package net.nachtbeere.minecraft.purifier

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import io.javalin.core.security.SecurityUtil
import io.javalin.http.Context
import io.javalin.plugin.json.JavalinJson
import io.javalin.plugin.json.FromJsonMapper
import io.javalin.plugin.json.ToJsonMapper
import io.javalin.plugin.openapi.OpenApiOptions
import io.javalin.plugin.openapi.OpenApiPlugin
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License

class PurifierServer(private val port: Int,
                     private val isDebug: Boolean,
                     val auth: Auth,
                     val cors: Cors,
                     val pluginInstance: Purifier)  {
    /* TODO: Add JWT or something to authorize request
        fixed token in config.yml(for access from another server)
        one-time token show in initialize phase.
       TODO: Make separated API access log
     */
    private var gson: Gson = GsonBuilder().serializeNulls().create()
    private var app: Javalin = Javalin.create { config: JavalinConfig ->
        config.defaultContentType = "application/json"
        config.contextPath = "/"
        config.registerPlugin(OpenApiPlugin(getOpenApiOptions()))
        config.accessManager { handler, ctx, permittedRoles ->
            auth.verify(handler, ctx, permittedRoles)
        }

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
        if (isDebug) {
            app.before { ctx -> log.info(ctx.req.pathInfo) }
        }
        if (cors.enabled()) {
            app.before { ctx -> cors.verify(ctx) }
        }
//        if (auth.enabled()) {
//            app.before { ctx -> auth.verify(ctx) }
//        }
        app.get("/") { ctx: Context -> ctx.result("Purifier - Modded Minecraft Server API") }
        app.post("/auth") { ctx: Context -> auth.authorize(ctx) }
    }

    fun start() {
        route(app)
        app.start(port)
        Thread.currentThread().contextClassLoader = classLoader
    }

    fun stop() {
        app.stop()
    }
}
