package com.example.njelib.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.njelib.Service.SharedPrefManager
import com.example.njelib.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding.imageView3.alpha = 0f
        binding.imageView3.animate().setDuration(1500).alpha(1f).withEndAction {
            if(SharedPrefManager.isLoggedIn(this)){
              if(SharedPrefManager.isAdmin(this)){
                  val intent= Intent(this, AdminActivity::class.java)
                  startActivity(intent)
                  overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                  finish()
              }
              else{
            val intent= Intent(this, UserActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
            }}else{
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
        }
    }
}