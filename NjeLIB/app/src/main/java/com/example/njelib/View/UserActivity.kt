package com.example.njelib.View

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.njelib.R
import com.example.njelib.Service.RetrofitInstance
import com.example.njelib.Service.SharedPrefManager
import com.example.njelib.dataModel.LibraryDatas
import com.example.njelib.dataModel.User
import com.example.njelib.databinding.ActivityUserBinding
import com.example.njelib.greeting
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var libraryDatas: LibraryDatas
    private lateinit var user: User
    private lateinit var handler: Handler
    private lateinit var handler2: Handler
    private lateinit var pollingRunnable: Runnable
    private lateinit var durumRunnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if(!SharedPrefManager.isLoggedIn(this)){
            val intent= Intent(this@UserActivity,MainActivity::class.java)
            finish()
            startActivity(intent)
        }
        binding.TimeText.text= greeting(this)
        binding.userNameText.text= SharedPrefManager.getUserName(this)
        val writer=QRCodeWriter()
        val bitMatrix=writer.encode(
            SharedPrefManager.getUserName(this@UserActivity),
            BarcodeFormat.QR_CODE,512,512)
        bitmapConverter(bitMatrix)
        val pollingIntervalMillis =500
        binding.bookView.isVisible=false
        binding.textView23.isVisible=false
        handler = Handler(Looper.getMainLooper()) //Update datas each of 1 seconds
        handler2=Handler(Looper.getMainLooper()) //For watching data that comes from statu of user
        pollingRunnable = object : Runnable {
            override fun run() {
                //Update datas each of 1 seconds
                try {
                    val call = RetrofitInstance.getApi().getLibDatas()
                    call.enqueue(object : Callback<LibraryDatas>{
                        override fun onResponse(
                            call: Call<LibraryDatas>,
                            response: Response<LibraryDatas>
                        ) {
                            val responseBody=response.body()
                            if (response.isSuccessful){
                                responseBody?.let {
                                    libraryDatas=it
                                }
                                if(libraryDatas.error==false){
                                    binding.currentUserText.text=libraryDatas.currentUser.toString()
                                    binding.CapacityText.text=libraryDatas.capacity.toString()
                                    SharedPrefManager.StoreLibDatas(libraryDatas.capacity,libraryDatas.currentUser,this@UserActivity)
                                }else{
                                    Toast.makeText(this@UserActivity,libraryDatas.message,Toast.LENGTH_LONG).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<LibraryDatas>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })
                } catch (e: Exception) {
                   e.printStackTrace()
                }
                handler.postDelayed(this, pollingIntervalMillis.toLong())
            }
        }
        durumRunnable= object :Runnable{
            override fun run() {
                try {
                    val string= SharedPrefManager.getUserName(this@UserActivity)
                    val call=RetrofitInstance.getApi().getDurum(string)
                    call.enqueue(object :Callback<User>{
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            val responseBody=response.body()
                            if(response.isSuccessful){
                             responseBody?.let {
                                user=it
                             }
                               if(user.error==false){
                                   SharedPrefManager.setDurum(this@UserActivity,user.durum)
                                   if(user.durum==0){
                                       binding.Bookbutton.isEnabled=true
                                       binding.bookView.isVisible=false
                                       binding.textView23.isVisible=false
                                   }else{
                                       binding.Bookbutton.isEnabled=false
                                       binding.bookView.isVisible=true
                                       binding.textView23.isVisible=true
                                   }
                               }else{
                                   Toast.makeText(this@UserActivity,user.message,Toast.LENGTH_LONG).show()
                               }
                            }
                        }
                        override fun onFailure(call: Call<User>, t: Throwable) {
                            t.printStackTrace()
                        }

                    })
                }catch (e:Exception){
                    e.printStackTrace()
                }
                handler2.postDelayed(this,pollingIntervalMillis.toLong())
            }
        }
        startPollings() //start Handlers
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       menuInflater.inflate(R.menu.user_menu,menu)
       return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item_logOut->{
                finish()
                SharedPrefManager.userLogOut(this)
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }
    fun bookPlace(view: View){
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Reservation")
        alertDialogBuilder.setMessage("Are you sure that you want to book a place from library?")
        alertDialogBuilder.setPositiveButton("YES") { dialog, which ->
            if(SharedPrefManager.getCurrentUser(this@UserActivity)< SharedPrefManager.getCapacity(this@UserActivity)){
            val call=RetrofitInstance.getApi().updateCurrentUser(12)
            call.enqueue(object :Callback<LibraryDatas>{
                override fun onResponse(call: Call<LibraryDatas>, response: Response<LibraryDatas>) {
                    val responseBody=response.body()
                    if(response.isSuccessful){
                        responseBody?.let {
                            libraryDatas=it
                            binding.currentUserText.text=libraryDatas.currentUser.toString()
                        }
                    }
                }
                override fun onFailure(call: Call<LibraryDatas>, t: Throwable) {
                    t.printStackTrace()
                }

            })
            val call1=RetrofitInstance.getApi().updateDurum(SharedPrefManager.getUserName(this@UserActivity),1)
            call1.enqueue(object :Callback<User>{
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    val responseBody=response.body()
                    if (response.isSuccessful){
                        responseBody?.let {
                            user=it
                        }
                        binding.Bookbutton.isEnabled=false
                    }
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    t.printStackTrace()
                }
            })

        }
        else{
            Toast.makeText(this@UserActivity,"CAPACITY IS FULL!!",Toast.LENGTH_LONG).show()
        }
        }
        alertDialogBuilder.setNegativeButton("NO") { dialog, which ->

        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()

    }
    fun bitmapConverter(bitMatrix: BitMatrix){
        try{
           val width=bitMatrix.width
           val height=bitMatrix.height
           val bmp=Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565)
           for (x in 0 until width){
               for(y in 0 until height){
                   bmp.setPixel(x,y,if (bitMatrix[x,y])Color.BLACK else Color.WHITE)
               }
           }
            binding.bookView.setImageBitmap(bmp)
        }catch (e:Exception){}
    }
 fun startPollings(){
     handler2.post(durumRunnable)
     handler.post(pollingRunnable)
 }
    fun stopPollings(){
        handler.removeCallbacks(pollingRunnable)
        handler2.removeCallbacks(durumRunnable)
    }
    override fun onPause() {
        super.onPause()
        stopPollings()//For stopping polings
    }

    override fun onStop() {
        super.onStop()
        stopPollings()
    }

    override fun onResume() {
        super.onResume()
        startPollings()
    }
}