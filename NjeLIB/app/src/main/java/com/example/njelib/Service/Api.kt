package com.example.njelib.Service

import com.example.njelib.dataModel.ErrorModel
import com.example.njelib.dataModel.LibraryDatas
import com.example.njelib.dataModel.User
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST("registerUser.php")
    fun createUser(@Field("username") username:String,@Field("email") email:String,@Field("durum")
    durum:Int,@Field("şifre") sifre:String) : Call<ErrorModel>
  // 10.22.242.208
    @FormUrlEncoded
    @POST("userLogin.php")
    fun userLogin(@Field("username") username: String,@Field("password") password:String):Call<User>

    @GET("getLibraryDatas.php")
    fun getLibDatas():  Call<LibraryDatas>

    @FormUrlEncoded
    @POST("updateCurrentUser.php")
    fun updateCurrentUser(@Field("currentuser")currentUser:Int):Call<LibraryDatas>

    @FormUrlEncoded
    @POST("getDurum.php")
    fun getDurum(@Field("username")username: String?):Call<User>

    @FormUrlEncoded
    @POST("updateDurum.php")
    fun updateDurum(@Field("username")username: String?,@Field("durum")durum: Int):Call<User>

    @FormUrlEncoded
    @POST("updateCapacity.php")
    fun updateCapacity(@Field("capacity") capacity:Int):Call<LibraryDatas>

    @POST("resetCurrentUser.php")
    fun resetCurrentUser():Call<ErrorModel>

    @FormUrlEncoded
    @POST("scanDurum.php")
    fun scanDurum(@Field("username")username: String):Call<ErrorModel>
}