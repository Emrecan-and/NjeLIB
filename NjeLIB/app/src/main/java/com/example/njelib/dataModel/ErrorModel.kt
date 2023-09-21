package com.example.njelib.dataModel

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("error")
    val error:Boolean,
    @SerializedName("message")
    val message:String
)