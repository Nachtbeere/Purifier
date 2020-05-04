package net.nachtbeere.minecraft.purifier

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.time.Instant
import java.time.OffsetDateTime
import java.util.*

typealias CachedUsers = MutableMap<String, CachedUser>
typealias RawCachedUsers = List<Map<String, String>>

class CachedUser(val name: String, val uuid: UUID, val expiresOn: Instant)

class PurifierUserCache() {
    private var gson: Gson = GsonBuilder().serializeNulls().create()
    private var users: CachedUsers = mutableMapOf()


    fun isCached(username: String): Boolean {
        return users[username] != null
    }

    private fun convertToIsoFormat(dateTime: String?): String {
        /*
        * Please use always "ISO FORMAT TIME" It's STANDARD.
         */
        return try {
            val parsed = dateTime!!.split(" ")
            val offsetPrefix = if (parsed[2].contains("+")) "+" else "-"
            val zoneOffset = parsed[2].replace(offsetPrefix, "").chunked(2)
            parsed[0].plus("T")
                .plus(parsed[1])
                .plus(offsetPrefix)
                .plus(zoneOffset[0])
                .plus(":")
                .plus(zoneOffset[1])
        } catch (e: Throwable) {
            OffsetDateTime.MAX.toString()
        }
    }

    private fun parseDateTime(dateTime: String?): Instant {
        return OffsetDateTime.parse(convertToIsoFormat(dateTime)).toInstant()
    }

    fun updateCache() {
        val cachePath = File(File(".").absolutePath, "usercache.json")
        if (cachePath.exists()) {
            val rawUsers = gson.fromJson(cachePath.readText(), List::class.java) as RawCachedUsers
            rawUsers.forEach {
                users[it["name"]?.toLowerCase() ?: error("")] = CachedUser(
                    name = it["name"] ?: error(""),
                    uuid = UUID.fromString(it["uuid"]) ?: error(UUID.randomUUID()),
                    expiresOn = parseDateTime(it["expiresOn"])
                )
            }
        }
    }

    private fun isExpired(expiresOn: Instant): Boolean {
        return OffsetDateTime.now().toInstant() > expiresOn
    }

    fun user(username: String): CachedUser? {
        val currentUser: CachedUser? = users[username]
        return if (currentUser != null && isExpired(currentUser.expiresOn)) {
            updateCache()
            user(username)
        } else {
            currentUser
        }
    }
}

