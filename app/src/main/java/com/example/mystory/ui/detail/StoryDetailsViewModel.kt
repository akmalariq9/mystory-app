package com.example.mystory.ui.detail

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.remote.api.ApiConfig
import com.example.mystory.data.remote.response.StoryDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryDetailsViewModel : ViewModel() {
	
	private var _storyDetailsResponse = MutableLiveData<StoryDetailsResponse?>()
	val storyDetailsResponse: LiveData<StoryDetailsResponse?> = _storyDetailsResponse
	
	private val _isLoading = MutableLiveData<Boolean>()
	val isLoading: LiveData<Boolean> = _isLoading
	
	fun getDetails(id: String) {
		_isLoading.value = true
		val client = ApiConfig.getApiService().getStoryDetails(id)
		client.enqueue(object : Callback<StoryDetailsResponse> {
			override fun onResponse(
                call: Call<StoryDetailsResponse>,
                response: Response<StoryDetailsResponse>
			) {
				_isLoading.value = false
				
				val responseBody = response.body()
				_storyDetailsResponse.value = responseBody
			}
			
			override fun onFailure(call: Call<StoryDetailsResponse>, t: Throwable) {
				_isLoading.value = false
				Toast.makeText(null, "onFailure ${t.message.toString()}", Toast.LENGTH_SHORT).show()
			}
		})
	}
}