package com.dicoding.aplikasistoryapppt2.data

import android.content.Context
import com.dicoding.aplikasistoryapppt2.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository(database, apiService)
    }
}