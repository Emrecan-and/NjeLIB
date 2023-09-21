package com.example.njelib.dataModel

import com.google.gson.annotations.SerializedName

data class LibraryDatas(
  @SerializedName("error")
    val error:Boolean,
  @SerializedName("message")
    val message:String,
  @SerializedName("capacity")
  val capacity:Int,
  @SerializedName("currentuser")
  val currentUser:Int,
)