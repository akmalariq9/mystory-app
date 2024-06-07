package com.example.mystory.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mystory.R
import com.example.mystory.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
	
	private lateinit var binding: ActivityRegisterBinding
	private val registerViewModel by viewModels<RegisterViewModel>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityRegisterBinding.inflate(layoutInflater)
		setContentView(binding.root)
		
		supportActionBar?.hide()
		
		binding.buttonSignup.setOnClickListener {
			if(binding.edRegisterName.error == null &&
				binding.edRegisterEmail.error == null &&
				binding.edRegisterPassword.error == null) {
				
				val name = binding.edRegisterName.text.toString()
				val email = binding.edRegisterEmail.text.toString()
				val password = binding.edRegisterPassword.text.toString()
				
				registerViewModel.getRegisterResponse(name, email, password)
				
				registerViewModel.register.observe(this) {
					if(!it!!.error) {
						Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
						backToLogin()
					}
				}
				
				registerViewModel.errorMessage.observe(this) { errorMessage ->
					if(errorMessage != null) {
						
						val message = if(errorMessage == RegisterViewModel.LOGIN_FAILURE) {
							getString(R.string.systemFailure)
						} else {
							errorMessage
						}
						Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
						registerViewModel.clearErrorMessage()
					}
				}
			}
			
		}
		
		binding.tvSignInNow.setOnClickListener {
			backToLogin()
		}
		
		registerViewModel.isLoading.observe(this) {
			showLoading(it)
		}
	}
	
	private fun backToLogin() {
		val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
		startActivity(intent)
	}
	
	private fun showLoading(isLoading: Boolean) {
		binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
	}
}