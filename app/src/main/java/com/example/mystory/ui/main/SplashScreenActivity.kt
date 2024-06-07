package com.example.mystory.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mystory.R
import com.example.mystory.ui.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        CoroutineScope(Dispatchers.Main).launch {
            delay(splashTimeOut) // Delay for 3 seconds
            checkSession()
        }
    }

    private fun checkSession() {
        val sharedPreferences = getSharedPreferences("mystory-data", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("authToken", null)

        if (authToken.isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}
