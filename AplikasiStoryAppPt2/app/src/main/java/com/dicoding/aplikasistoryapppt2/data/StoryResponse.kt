package com.dicoding.aplikasistoryapppt2.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("message")
    val message: String
)

@Entity(tableName = "story")
data class ItemStoryResponse(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("lat")
    val lat: Double?,

    @field:SerializedName("lon")
    val lon: Double?
)

data class AllStoryResponse(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<ItemStoryResponse>
)

data class DetailStoryResponse(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("story")
    val story: ItemStoryResponse
)
