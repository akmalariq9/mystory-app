package com.example.mystory.di

import android.content.Context
import com.example.mystory.data.mediator.StoryRepository
import com.example.mystory.data.local.room.StoryRoomDatabase
import com.example.mystory.data.remote.api.ApiConfig

object Injection {
    @JvmStatic
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryRoomDatabase.getInstance(context)
        return StoryRepository(apiService, database)
    }
}