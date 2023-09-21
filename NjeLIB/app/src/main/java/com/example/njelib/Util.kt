package com.example.njelib

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.njelib.View.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun isEmailValid(email: String): Boolean {
    val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}".toRegex()
    return emailPattern.matches(email)
}
fun greeting(context: Context):String{
    val stringArray=context.resources.getStringArray(R.array.greetings)
    val currentTime = Calendar.getInstance().time
    val sdf = SimpleDateFormat("HH", Locale.getDefault())
    val currentHour = sdf.format(currentTime).toInt()
    when(currentHour){
        in 0..11 -> return stringArray[0]
        in 12..16 -> return stringArray[1]
        in 17..20 -> return stringArray[2]
        else -> return stringArray[3]
    }
}
fun Context.isPermissionGranted(permission: String):Boolean{
    return ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED
}
inline fun Context.cameraPermissionRequest(crossinline positive:()->Unit){
    AlertDialog.Builder (this).setTitle("Camera Permission Required")
        .setMessage("Without accessing the camera it is not possible to SCAN..")
        .setPositiveButton("Allow Camera"){
                dialog,which->positive.invoke()
        }.setNegativeButton("Cancel"){
                dialog,which->
        }.show()
}
fun Context.openPermissionSetting(){
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also{
        val uri: Uri = Uri.fromParts("package",packageName,null)
        it.data=uri
        startActivity(it)
    }}
