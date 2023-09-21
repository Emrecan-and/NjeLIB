package com.example.njelib.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.njelib.dataModel.ErrorModel
import com.example.njelib.Service.RetrofitInstance
import com.example.njelib.databinding.ActivityMainBinding
import com.example.njelib.isEmailValid
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var errorModel: ErrorModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

       }
    fun register(view:View){
            if(!binding.userNameEditText.text.isEmpty() and !binding.editTextTextPassword.text.isEmpty()
                and !binding.editTextTextEmailAddress.text.isEmpty()){
                if(isEmailValid(binding.editTextTextEmailAddress.text.toString().trim())){
            val apiService= RetrofitInstance.getApi()
            val call=apiService.createUser(binding.userNameEditText.text.toString().trim(),binding.editTextTextEmailAddress.text.toString().trim(),0,binding.editTextTextPassword.text.toString().trim())
            call.enqueue(object : Callback<ErrorModel>{
                override fun onResponse(
                    call: Call<ErrorModel>,
                    response: Response<ErrorModel>
                ) {
                    val responseBody=response.body()
                    if (response.isSuccessful) {
                        responseBody?.let {
                            errorModel=it
                        }
                        if(errorModel.error==false){
                            val intent=Intent(this@MainActivity,LoginActivity::class.java)
                            finish()
                            startActivity(intent)
                        }
                        Toast.makeText(this@MainActivity,errorModel.message,Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<ErrorModel>, t: Throwable) {
                    // Toast.makeText(this@MainActivity,"Başaramadık",Toast.LENGTH_LONG).show()
                    t.printStackTrace()
                }
            })}else{
                    Toast.makeText(this,"Wrong Mail!!",Toast.LENGTH_LONG).show()
            }
        }else{
          Toast.makeText(this,"Empty field!!",Toast.LENGTH_LONG).show()
        }
    }
  fun goToLogin(view:View){
      val intent=Intent(this,LoginActivity::class.java)
      finish()
      startActivity(intent)
  }
}