package net.nachtbeere.minecraft.purifier

import net.nachtbeere.minecraft.purifier.models.AuthorizeUserModel
import net.nachtbeere.minecraft.purifier.models.TokenResponseModel
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.Handler
import io.javalin.http.UnauthorizedResponse
import io.javalin.plugin.openapi.annotations.*
import org.bukkit.configuration.MemorySection
import org.eclipse.jetty.http.HttpStatus
import java.util.logging.Logger


class Cors(private val log: Logger, private val config: MemorySection) {
    private val enabled = config.getBoolean("enabled")
    private val hosts = config.getList("hosts")

    fun enabled(): Boolean {
        return this.enabled
    }

    fun verify(ctx: Context) {
    }
}



class Auth(private val log: Logger, private val config: MemorySection) {
    private val skipAuthPath = listOf("/", "/auth", "/api-docs")
    private val algorithm: Algorithm = Algorithm.HMAC256(config.getString("key"))
    private val issuer: String = config.getString("issuer")!!
    private val verifier: JWTVerifier = JWT.require(this.algorithm).withIssuer(this.issuer).build()
    private val enabled = config.getBoolean("enabled")
    private val users: HashMap<String, AuthUser> = HashMap()

    init {
        for (user in config.getList("users")!!) {
            if (user != null) {
                val u = AuthUser.ModelMapper.from(user as LinkedHashMap<String, HashMap<String, Any>>)
                users[u.name] = u
            }
        }
    }

    private fun user(username: String): AuthUser? {
        return users[username]
    }

    private fun generate(username: String): TokenResponseModel {
        return try {
            val token = JWT.create()
                .withIssuer(this.issuer)
                .withClaim("username", username)
                .sign(this.algorithm)
            TokenResponseModel(token=token)
        } catch (exception: JWTCreationException) {
            TokenResponseModel(token=null)
        }
    }

    private fun sliceBearerToken(token: String): List<String> {
        return token.split(" ")
    }

    private fun verifyToken(token: String?): DecodedJWT? {
        return if (token != null) {
            val slicedToken = sliceBearerToken(token)
            if (slicedToken[0] == "Bearer") {
                try {
                    verifier.verify(slicedToken[1])
                } catch (exception: JWTVerificationException) {
                    null
                }
            } else {
                null
            }
        } else {
            null
        }
    }

    @OpenApi(
        requestBody = OpenApiRequestBody(content=[OpenApiContent(AuthorizeUserModel::class)]),
        responses = [
            OpenApiResponse(status = HttpStatus.OK_200.toString(),
                content = [OpenApiContent(TokenResponseModel::class)]),
            OpenApiResponse(status = HttpStatus.UNAUTHORIZED_401.toString())
        ]
    )
    fun authorize(ctx: Context) {
        val requestUser = ctx.bodyAsClass(AuthorizeUserModel::class.java)
        val user = this.user(requestUser.username)
        if (user != null && user.password == requestUser.password) {
            ctx.json(this.generate(user.name))
        } else {
            ctx.status(HttpStatus.UNAUTHORIZED_401)
        }
    }

    @OpenApi(
        headers = [
            OpenApiParam("Authorization", String::class, "Authorization: Bearer {token}")
        ]
    )
    fun verify(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
        if (!enabled || permittedRoles.contains(Permission.ANON) || skipAuthPath.contains(ctx.path())) {
            handler.handle(ctx)
        } else {
            val token = ctx.header("Authorization")
            try {
                val username = this.verifyToken(token)!!.getClaim("username")!!.asString()
                val user = this.user(username)
                if (user!!.roles.contains("ADMIN") || user.roles.contains(permittedRoles.first().toString())) {
                    handler.handle(ctx)
                } else {
                    throw ForbiddenResponse()
                }
            } catch (exception: NullPointerException) {
                throw UnauthorizedResponse()
            }
        }
    }
}