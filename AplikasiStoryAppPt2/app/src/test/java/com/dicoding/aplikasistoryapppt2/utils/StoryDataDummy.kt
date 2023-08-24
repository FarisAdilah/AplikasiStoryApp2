package com.dicoding.aplikasistoryapppt2.utils

import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse

object StoryDataDummy {
    fun generateDummyStoryEntity(): List<ItemStoryResponse> {
        val storyList: MutableList<ItemStoryResponse> = arrayListOf()
        for (i in 0..5) {
            val story = ItemStoryResponse(
                "$i",
                "Story Name",
                "Story Description",
                "Photo Url",
                "Date Created",
                null,
                null
            )
            storyList.add(story)
        }
        return storyList
    }
}