package com.example.mystory.data.remote.api

import com.example.mystory.data.remote.response.LoginResponse
import com.example.mystory.data.remote.response.RegisterResponse
import com.example.mystory.data.remote.response.StoryDetailsResponse
import com.example.mystory.data.remote.response.FileUploadResponse
import com.example.mystory.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
	
	@FormUrlEncoded
	@POST("/v1/login")
	fun loginUser(
		@Field("email") email: String,
		@Field("password") password: String,
	): Call<LoginResponse>
	
	@FormUrlEncoded
	@POST("/v1/register")
	fun registerUser(
		@Field("name") name: String,
		@Field("email") email: String,
		@Field("password") password: String,
	): Call<RegisterResponse>

	@GET("/v1/stories")
	suspend fun getStoriesWithPage(
		@Query("page") page: Int,
		@Query("size") size: Int,
	): StoryResponse

	@GET("/v1/stories")
	fun getStoriesWithSize(
		@Query("size") size: Int,
		@Query("location") location: Int = 1,
	): Call<StoryResponse>

	@GET("/v1/stories/{id}")
	fun getStoryDetails(@Path("id") id: String): Call<StoryDetailsResponse>

	@Multipart
	@POST("/v1/stories")
	fun uploadImage(
		@Part file: MultipartBody.Part,
		@Part("description") description: RequestBody,
	): Call<FileUploadResponse>

	@Multipart
	@POST("/v1/stories")
	fun uploadImageWithLoc(
		@Part file: MultipartBody.Part,
		@Part("description") description: RequestBody,
		@Part("lat") lat: RequestBody,
		@Part("lon") lon: RequestBody,
	): Call<FileUploadResponse>
}