package com.dicoding.aplikasistoryapppt2.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.aplikasistoryapppt2.data.Injection
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.data.StoryRepository
class StoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    var story: LiveData<PagingData<ItemStoryResponse>>? = null
    private var token: String = "Bearer "

    fun setToken(newToken: String) {
        token = newToken
    }

    fun getStory() {
        Log.i("TOKEN", "token adalah: $token")
        story = storyRepository.getStories(token).cachedIn(viewModelScope)
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoryViewModel(Injection.provideRepository(context)) as T
        }
        throw java.lang.IllegalArgumentException("Unknown ViewModel class")
    }
}