package com.dicoding.aplikasistoryapppt2.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.aplikasistoryapppt2.api.ApiConfig
import com.dicoding.aplikasistoryapppt2.data.DetailStoryResponse
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailStoryViewModel : ViewModel() {
    private val _detailStory = MutableLiveData<ItemStoryResponse>()
    val detailStory: LiveData<ItemStoryResponse> = _detailStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading

    private var token: String = ""
    private var storyId: String = ""

    private fun getDetailStory() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailStory(token, storyId)
        client.enqueue(object : Callback<DetailStoryResponse> {
            override fun onResponse(
                call: Call<DetailStoryResponse>,
                response: Response<DetailStoryResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _detailStory.value = responseBody.story
                } else {
                    Log.i(DetailStoryActivity.TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.i(DetailStoryActivity.TAG, "onFailure: ${t.message}")
            }

        })
    }

    fun updateParam(newToken: String, newStoryId: String) {
        token = newToken
        storyId = newStoryId
        getDetailStory()
    }
}