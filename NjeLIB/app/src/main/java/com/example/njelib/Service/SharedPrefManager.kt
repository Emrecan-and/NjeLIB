package com.example.njelib.Service

import android.content.Context

object  SharedPrefManager {
    private val SHARED_PREF_NAME ="mysharedpref13"
    private val KEY_USERNAME="username"
    private val KEY_EMAIL="email"
    private val KEY_DURUM="durum"

    fun userLogIn(context: Context,username:String,email:String,durum:Int):Boolean{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putInt(KEY_DURUM,durum)
        editor.putString(KEY_USERNAME,username)
        editor.putString(KEY_EMAIL,email)
        editor.apply()
        return true
    }
    fun isLoggedIn(context: Context):Boolean{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        if(sharedPreferences.getString(KEY_USERNAME,null)==null){
            return false
        }
        return true
    }
    fun userLogOut(context: Context):Boolean{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.clear()
        editor.apply()
        return true
    }
    fun getUserName(context: Context):String?{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USERNAME,null)
    }
    fun getMail(context: Context):String?{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_EMAIL,null)
    }
    fun getDurum(context: Context):Int{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_DURUM,0)
    }
    fun setDurum(context: Context,durum: Int){
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putInt(KEY_DURUM,durum)
        editor.apply()
    }
    fun StoreLibDatas(capacity:Int,currentUser:Int,context: Context):Boolean{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor=sharedPreferences.edit()
        editor.putInt("capacity",capacity)
        editor.putInt("currentUser",currentUser)
        editor.apply()
        return true
    }
    fun getCapacity(context: Context):Int{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getInt("capacity",0)
    }
    fun getCurrentUser(context: Context):Int{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        return sharedPreferences.getInt("currentUser",0)
    }
    fun isAdmin(context: Context):Boolean{
        val sharedPreferences=context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val a =sharedPreferences.getString("username","")
        val b=sharedPreferences.getString("email","")
        if(a=="Librarian" && b=="gamf.szakk@kik.kefo.hu"){
            return true
        }else{
            return false
        }
    }
}