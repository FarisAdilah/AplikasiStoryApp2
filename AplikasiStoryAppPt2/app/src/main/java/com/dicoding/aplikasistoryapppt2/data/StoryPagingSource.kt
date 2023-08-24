package com.dicoding.aplikasistoryapppt2.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.aplikasistoryapppt2.api.ApiService

class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, ItemStoryResponse>() {

    override fun getRefreshKey(state: PagingState<Int, ItemStoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemStoryResponse> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(token, page, params.loadSize, null).listStory

            LoadResult.Page(
                data = responseData,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}