package com.example.mystory

import com.example.mystory.data.local.entity.StoryEntity

object DataDummy {

    fun generateDummyStoryEntity(): List<StoryEntity> {
        val storyListCheck = ArrayList<StoryEntity>()
        for (i in 0..100) {
            val story = StoryEntity(
                "ini id-$i",
                "Sender$i",
                "Testing123 halo halo ke -$i",
                "https://story-api.dicoding.dev/images/stories/photos-1717514231509_20ea22451ec5405b15b4.jpg",
                "2024-06-04T15:17:11.510Z",
                0.3f + i.toFloat(),
                0.55f
            )
            storyListCheck.add(story)
        }
        return storyListCheck
    }
}