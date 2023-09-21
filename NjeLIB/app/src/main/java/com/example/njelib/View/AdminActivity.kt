package com.example.njelib.View

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.njelib.R
import com.example.njelib.Service.RetrofitInstance
import com.example.njelib.Service.SharedPrefManager
import com.example.njelib.cameraPermissionRequest
import com.example.njelib.dataModel.ErrorModel
import com.example.njelib.dataModel.LibraryDatas
import com.example.njelib.databinding.ActivityAdminBinding
import com.example.njelib.greeting
import com.example.njelib.isPermissionGranted
import com.example.njelib.openPermissionSetting
import com.google.mlkit.vision.barcode.common.Barcode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var handler:Handler
    private lateinit var pollingRunnable:Runnable
    private lateinit var libraryDatas: LibraryDatas
    private lateinit var errorModel: ErrorModel
    private val cameraPermission=android.Manifest.permission.CAMERA
    private val requestPermissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted->
        if(isGranted){
            startScanner()
        }
    }
    val pollingIntervalMillis =500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.TimeText.text= greeting(this)
        binding.userNameText.text= SharedPrefManager.getUserName(this)
        handler = Handler(Looper.getMainLooper())
        pollingRunnable = object : Runnable {
            override fun run() {
                //Update datas each of 1 seconds
                try {
                    val call = RetrofitInstance.getApi().getLibDatas()
                    call.enqueue(object : Callback<LibraryDatas> {
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
                                    SharedPrefManager.StoreLibDatas(libraryDatas.capacity,libraryDatas.currentUser,this@AdminActivity)
                                }else{
                                    Toast.makeText(this@AdminActivity,libraryDatas.message, Toast.LENGTH_LONG).show()
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
        startPolling()
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
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else-> return super.onOptionsItemSelected(item)
        }
    }
    fun resetCurrentUser(view: View){
       val alertDialogBuilder=AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("RESET")
        alertDialogBuilder.setMessage("Are you sure that you want to reset current number of people in library?")
        alertDialogBuilder.setPositiveButton("YES"){
            dialog,which->
            val call=RetrofitInstance.getApi().resetCurrentUser()
            call.enqueue(object :Callback<ErrorModel>{
                override fun onResponse(call: Call<ErrorModel>, response: Response<ErrorModel>) {
                    val responseBody=response.body()
                    if(response.isSuccessful){
                        responseBody?.let {
                            errorModel=it
                        }
                        Toast.makeText(this@AdminActivity,errorModel.message,Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ErrorModel>, t: Throwable) {
                    t.printStackTrace()
                }

            })
        }
        alertDialogBuilder.setNegativeButton("NO"){dialog,which->}
        val alertDialog=alertDialogBuilder.create()
        alertDialog.show()
    }
    fun changeCapacity(view:View){
        val call=RetrofitInstance.getApi().updateCapacity(binding.editTextNumber.text.toString().toInt())
        call.enqueue(object :Callback<LibraryDatas>{
            override fun onResponse(call: Call<LibraryDatas>, response: Response<LibraryDatas>) {
                val responseBody=response.body()
                if(response.isSuccessful){
                    responseBody?.let {
                        libraryDatas=it
                    }
                    Toast.makeText(this@AdminActivity,libraryDatas.message,Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LibraryDatas>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }
    fun scanReservation(view:View){
        requestCameraAndStartScanner()
    }
    fun startPolling(){
        handler.post(pollingRunnable)
    }
    fun stopPolling(){
        handler.removeCallbacks(pollingRunnable)
    }
    override fun onPause() {
        super.onPause()
        stopPolling()//For stopping polings
    }

    override fun onStop() {
        super.onStop()
        stopPolling()
    }

    override fun onResume() {
        super.onResume()
        startPolling()
    }
    private fun requestCameraAndStartScanner(){
        if(isPermissionGranted(cameraPermission)){
            startScanner()
        }
        else{
            requestCameraPermission()
        }
    }
    private fun requestCameraPermission(){
        when{
            shouldShowRequestPermissionRationale(cameraPermission) ->{
                cameraPermissionRequest {
                    openPermissionSetting()
                }
            }
            else ->{
                requestPermissionLauncher.launch(cameraPermission)
            }
        }
    }
    private fun startScanner(){
        SecondActivity.startScanner(this) { barcodes ->
            barcodes.forEach() { barcode ->
                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {}
                    else -> {
                        val call=RetrofitInstance.getApi().scanDurum(barcode.rawValue.toString())
                        call.enqueue(object :Callback<ErrorModel>{
                            override fun onResponse(
                                call: Call<ErrorModel>,
                                response: Response<ErrorModel>
                            ) {
                             val responseBody=response.body()
                                if(response.isSuccessful){
                                    responseBody?.let {
                                        errorModel=it
                                    }
                                    if(errorModel.error){
                                        Toast.makeText(this@AdminActivity,errorModel.message,Toast.LENGTH_LONG).show()
                                    }else{
                                        val alertDialogBuilder=AlertDialog.Builder(this@AdminActivity)
                                        alertDialogBuilder.setTitle(barcode.rawValue.toString())
                                        alertDialogBuilder.setMessage(errorModel.message)//Burada
                                        alertDialogBuilder.setPositiveButton("OK"){ dialog,which->}
                                        val alertDialog=alertDialogBuilder.create()
                                        alertDialog.show()
                                    }
                                }
                            }
                            override fun onFailure(call: Call<ErrorModel>, t: Throwable) {
                                t.printStackTrace()
                            }

                        })
                    }
                }
            }
        }
    }

}