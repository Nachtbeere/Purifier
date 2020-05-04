package net.nachtbeere.minecraft.purifier.controllers

import net.nachtbeere.minecraft.purifier.models.CommonResponseModel

open class PurifierControllerBase() {
    fun successResponse(): CommonResponseModel {
        return CommonResponseModel(result = "SUCCESS")
    }

    fun failedResponse(): CommonResponseModel {
        return CommonResponseModel(result = "FAILED")
    }
}


