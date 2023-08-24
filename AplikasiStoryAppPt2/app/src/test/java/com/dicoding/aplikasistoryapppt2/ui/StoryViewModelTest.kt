package com.dicoding.aplikasistoryapppt2.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.data.StoryRepository
import com.dicoding.aplikasistoryapppt2.utils.MainDispatcherRule
import com.dicoding.aplikasistoryapppt2.utils.StoryDataDummy
import com.dicoding.aplikasistoryapppt2.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private val token = "Bearer Token"

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = StoryDataDummy.generateDummyStoryEntity()
        val data: PagingData<ItemStoryResponse> = StoryPagingSource.snapshot(dummyStory)
        val expectedStory = MutableLiveData<PagingData<ItemStoryResponse>>()
        expectedStory.value = data

        Mockito.`when`(storyRepository.getStories(token)).thenReturn(expectedStory)
        val storyViewModel = StoryViewModel(storyRepository)
        storyViewModel.story = storyRepository.getStories(token)
        val actualStory: PagingData<ItemStoryResponse>? = storyViewModel.story?.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        if (actualStory != null) {
            differ.submitData(actualStory)
        }

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ItemStoryResponse> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<ItemStoryResponse>>()
        expectedStory.value = data
        Mockito.`when`(storyRepository.getStories(token)).thenReturn(expectedStory)

        val storyViewModel = StoryViewModel(storyRepository)
        storyViewModel.story = storyRepository.getStories(token)
        val actualStory: PagingData<ItemStoryResponse>? = storyViewModel.story?.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        if (actualStory != null) {
            differ.submitData(actualStory)
        }

        Assert.assertEquals(0, differ.snapshot().size)
    }
}

class StoryPagingSource : PagingSource<Int, LiveData<List<ItemStoryResponse>>>() {
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ItemStoryResponse>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ItemStoryResponse>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<ItemStoryResponse>): PagingData<ItemStoryResponse> {
            return PagingData.from(items)
        }
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}

}
