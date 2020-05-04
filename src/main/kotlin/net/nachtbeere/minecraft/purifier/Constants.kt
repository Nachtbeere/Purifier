package net.nachtbeere.minecraft.purifier

import io.javalin.core.security.Role

class Constants() {
    companion object {
        const val packageName = "purifier"
        const val apiVersion = "v1"
        val time = TimeConstants
    }
}

class TimeConstants() {
    companion object {
        const val day = 1000.toLong()
        const val noon = 6000.toLong()
        const val night = 13000.toLong()
        const val midnight = 18000.toLong()
        const val min = 0.toLong()
        const val max = 24000.toLong()
    }
}

enum class PurifierGameMode(val value: Int) {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3);

    companion object {
        fun valueOf(value: Int) = values().find { it.value == value } ?: SURVIVAL
    }
}

enum class Permission : Role {
    ADMIN,
    WRITE,
    READ,
    ANON
}
