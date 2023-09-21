package com.example.njelib.dataModel

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("error")
        val error:Boolean,
        @SerializedName("message")
        val message:String,
        @SerializedName("username")
        val username:String,
        @SerializedName("email")
        val email:String,
        @SerializedName("durum")
        var durum:Int,
    )
