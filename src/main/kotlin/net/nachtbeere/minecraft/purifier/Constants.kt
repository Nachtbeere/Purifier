package net.nachtbeere.minecraft.purifier

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
    }
}