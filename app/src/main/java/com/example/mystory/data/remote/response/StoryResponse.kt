package com.example.mystory.data.remote.response

import com.example.mystory.data.local.entity.StoryEntity
import com.google.gson.annotations.SerializedName

data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<StoryItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class StoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Float = 0f,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Float = 0f

) {
	fun toEntity(): StoryEntity {
		return StoryEntity(
			this.id,
			this.name,
			this.description,
			this.photoUrl,
			this.createdAt,
			this.lat,
			this.lon,
		)
	}
}
