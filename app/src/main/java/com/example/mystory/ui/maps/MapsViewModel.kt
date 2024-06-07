package com.example.mystory.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mystory.data.mediator.StoryRepository
import com.example.mystory.data.remote.response.StoryItem
import com.example.mystory.data.remote.response.StoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(_storyRepository: StoryRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<StoryItem>>()
    val listStory: LiveData<List<StoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val storyRepository = _storyRepository

    init {
        getAllStory()
    }

    private fun getAllStory() {
        _isLoading.value = true
        val client = storyRepository.apiService.getStoriesWithSize(SIZE)
        client.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(
                call: Call<StoryResponse>,
                response: Response<StoryResponse>
            ) {
                _isLoading.value = false

                val responseBody = response.body()
                if(responseBody != null)
                    _listStory.value = responseBody.listStory
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure ${t.message.toString()}")
            }

        })
    }

    companion object {
        private const val SIZE = 100
        private const val TAG = "MapsViewModel"
    }
}