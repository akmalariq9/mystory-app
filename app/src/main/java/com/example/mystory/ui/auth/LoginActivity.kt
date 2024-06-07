package com.example.mystory.ui.auth

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystory.R
import com.example.mystory.data.remote.api.ApiConfig
import com.example.mystory.databinding.ActivityLoginBinding
import com.example.mystory.ui.main.MainActivity

class LoginActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityLoginBinding
	private lateinit var sharedPreferences: SharedPreferences
	private val loginViewModel by viewModels<LoginViewModel>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		supportActionBar?.hide()
		
		sharedPreferences = getSharedPreferences("mystory-data", MODE_PRIVATE)
		
		val auth = sharedPreferences.getString("authToken", null)
		if(auth != null) {
			ApiConfig.setAuth(auth)
			val intent = Intent(this@LoginActivity, MainActivity::class.java)
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
			startActivity(intent)
		}
		
		binding.buttonSignin.setOnClickListener {
			if(binding.edLoginEmail.error == null && binding.edLoginPassword.error == null) {
				val email = binding.edLoginEmail.text.toString()
				val password = binding.edLoginPassword.text.toString()
		
				loginViewModel.getLoginInfo(email, password)
				
				loginViewModel.login.observe(this) {
					if(!it!!.error) {
						val editor = sharedPreferences.edit()
						editor.putString("authToken", it.loginResult.token)
						editor.putString("name", it.loginResult.name)
						editor.putString("userId", it.loginResult.userId)
						editor.apply()
						
						Toast.makeText(this@LoginActivity, "You are signed in!", Toast.LENGTH_SHORT).show()
						
						val intent = Intent(this@LoginActivity, MainActivity::class.java)
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
						startActivity(intent)
					} else
						Toast.makeText(this, getString(R.string.invalidCredential), Toast.LENGTH_SHORT).show()
				}
				
				loginViewModel.errorMessage.observe(this) { errorMessage ->
					if(errorMessage != null) {
						val message = if(errorMessage == LoginViewModel.INVALID_CREDENTIALS) R.string.invalidCredential else R.string.systemFailure
						Toast.makeText(this, getString(message), Toast.LENGTH_SHORT).show()
						loginViewModel.clearErrorMessage()
					}
				}
			}
		}
		
		binding.tvSignUp.setOnClickListener {
			val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
			startActivity(intent)
		}
		
		loginViewModel.isLoading.observe(this) {
			showLoading(it)
		}
	}
	
	private fun showLoading(isLoading: Boolean) {
		binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
	}
}