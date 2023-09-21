package com.example.njelib.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.njelib.Service.RetrofitInstance
import com.example.njelib.Service.SharedPrefManager
import com.example.njelib.dataModel.User
import com.example.njelib.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


    }
    fun userLogin(view: View){
        if (!binding.userNameEditText.text.toString().isEmpty() and !binding.editTextTextPassword.text.isEmpty()){
            if(binding.userNameEditText.text.toString().trim()=="admin"){
                 if (binding.editTextTextPassword.text.toString().trim()=="23admin19"){
                     SharedPrefManager.userLogIn(this@LoginActivity, "Librarian","gamf.szakk@kik.kefo.hu",0)
                     val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                     finish()
                     startActivity(intent)
                 }
            }else {
                val call = RetrofitInstance.getApi().userLogin(
                    binding.userNameEditText.text.toString().trim(),
                    binding.editTextTextPassword.text.toString().trim()
                )
                call.enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        val responseBody = response.body()
                        if (response.isSuccessful) {
                            responseBody?.let {
                                user = it
                            }
                            if (user.error == false) {
                                SharedPrefManager.userLogIn(
                                    this@LoginActivity,
                                    user.username,
                                    user.email,
                                    user.durum
                                )
                                val intent = Intent(this@LoginActivity, UserActivity::class.java)
                                finish()
                                startActivity(intent)
                            }
                            Toast.makeText(this@LoginActivity, user.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }}else{
      Toast.makeText(this,"Empty Field",Toast.LENGTH_LONG).show()
    }
    }
    fun goToSignUp(view:View){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}