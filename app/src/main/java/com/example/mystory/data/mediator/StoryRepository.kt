package com.example.mystory.data.mediator

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.mystory.data.local.entity.StoryEntity
import com.example.mystory.data.local.room.StoryRoomDatabase
import com.example.mystory.data.remote.api.ApiService

class StoryRepository (
    val apiService: ApiService,
    private val database: StoryRoomDatabase
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getAllStories(): LiveData<PagingData<StoryEntity>> {

        return Pager(
            config = PagingConfig(
                pageSize = 10
            ),
            remoteMediator = StoryRemoteMediator(database, apiService),
            pagingSourceFactory = {
                database.storyDao().getAllStories()
            }
        ).liveData
    }
}