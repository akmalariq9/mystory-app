package com.example.mystory.data.remote.api

import com.example.mystory.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
	
	companion object {
		private var auth: String? = null
		fun getApiService(): ApiService {
			val loggingInterceptor = if(BuildConfig.DEBUG) {
				HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
			}else {
				HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
			}
			
			val authInterceptor = Interceptor { chain ->
				val req = chain.request()
				val requestHeaders = req.newBuilder()
					.addHeader("Authorization", "Bearer $auth")
					.build()
				chain.proceed(requestHeaders)
			}
			
			val client = OkHttpClient.Builder()
				.addInterceptor(loggingInterceptor)
				.addInterceptor(authInterceptor)
				.build()
			
			val retrofit = Retrofit.Builder()
				.baseUrl("https://story-api.dicoding.dev/")
				.addConverterFactory(GsonConverterFactory.create())
				.client(client)
				.build()
			
			return retrofit.create(ApiService::class.java)
		}
		
		fun setAuth(auth: String) {
			Companion.auth = auth
		}
		
		fun clearAuth() {
			auth = null
		}
	}
}