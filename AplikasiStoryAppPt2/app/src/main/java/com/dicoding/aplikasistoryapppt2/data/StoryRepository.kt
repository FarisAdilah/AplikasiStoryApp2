package com.dicoding.aplikasistoryapppt2.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.aplikasistoryapppt2.api.ApiService

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStories(token: String): LiveData<PagingData<ItemStoryResponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }
}