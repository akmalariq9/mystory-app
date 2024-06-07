package com.example.mystory.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystory.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(stories: List<StoryEntity>)

    @Query("DELETE FROM stories")
    suspend fun deleteAllStories()

    @Query("SELECT * FROM stories ORDER BY createdAt DESC")
    fun getAllStories(): PagingSource<Int, StoryEntity>
}