package com.dicoding.aplikasistoryapppt2.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.data.UserPreference
import com.dicoding.aplikasistoryapppt2.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private lateinit var mUserPreference: UserPreference
    private lateinit var detailStoryViewModel: DetailStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_ID).toString()

        mUserPreference = UserPreference(this)
        val token = "Bearer " + mUserPreference.getUser().token.toString()

        detailStoryViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[DetailStoryViewModel::class.java]
        detailStoryViewModel.updateParam(token, storyId)
        detailStoryViewModel.detailStory.observe(this) {
            setContentView(it)
        }

        detailStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setContentView(detail: ItemStoryResponse) {
        binding.tvDetailName.text = detail.name
        binding.tvDetailDescription.text = detail.description
        Glide
            .with(this)
            .load(detail.photoUrl)
            .fitCenter()
            .into(binding.ivDetailPhoto)
    }

    companion object {
        const val TAG = "DetailStoryActivity"
        const val EXTRA_ID = "extra_id"
    }
}