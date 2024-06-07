package com.example.mystory.ui.homepage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.remote.api.ApiConfig
import com.example.mystory.data.remote.response.FileUploadResponse
import com.example.mystory.ui.auth.RegisterViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {

	private val _upload = MutableLiveData<FileUploadResponse?>()
	val upload: MutableLiveData<FileUploadResponse?> = _upload

	private val _isLoading = MutableLiveData<Boolean>()
	val isLoading: LiveData<Boolean> = _isLoading

	private val _errorMessage = MutableLiveData<String?>()
	val errorMessage: MutableLiveData<String?> = _errorMessage

	fun getUploadResponse(imageMultipart: MultipartBody.Part, description: RequestBody, lat: RequestBody? = null, lon: RequestBody? = null) {

		_isLoading.value = true
		val client =
			if(lat != null && lon != null) {
				ApiConfig.getApiService().uploadImageWithLoc(imageMultipart, description, lat, lon)
			} else {
				ApiConfig.getApiService().uploadImage(imageMultipart, description)
			}
		client.enqueue(object : Callback<FileUploadResponse> {

			override fun onResponse(
				call: Call<FileUploadResponse>,
				response: Response<FileUploadResponse>
			) {
				_isLoading.value = false

				val responseBody = response.body()
				if(responseBody != null) {
					_upload.value = responseBody
				}
			}
			override fun onFailure(call: Call<FileUploadResponse>, t: Throwable) {
				_isLoading.value = false
				Log.e(TAG, "onFailure ${t.message.toString()}")
				_errorMessage.value = RegisterViewModel.LOGIN_FAILURE			}
		})
	}

	fun clearErrorMessage() {
		_errorMessage.value = null
	}

	companion object {
		const val TAG = "StoryAddViewModel"
	}
}