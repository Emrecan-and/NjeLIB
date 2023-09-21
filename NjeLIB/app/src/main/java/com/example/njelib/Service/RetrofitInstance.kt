package com.example.njelib.Service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val Base_URL="http://192.168.43.3/NJELIB/v1/"
    //Hotstop!
    val retrofit:Retrofit by lazy{
        Retrofit.Builder().baseUrl(Base_URL).
        addConverterFactory(GsonConverterFactory.create()).build()
    }
    fun getApi(): Api {
        return retrofit.create(Api::class.java)
    }

}