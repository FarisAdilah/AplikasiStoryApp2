package com.dicoding.aplikasistoryapppt2.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.aplikasistoryapppt2.R
import com.dicoding.aplikasistoryapppt2.data.UserModel
import com.dicoding.aplikasistoryapppt2.data.UserPreference
import com.dicoding.aplikasistoryapppt2.databinding.ActivityStoriesBinding

class StoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesBinding
    private lateinit var mUserPreference: UserPreference
    private lateinit var userModel: UserModel
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result: ActivityResult ->
        if (result.resultCode == AddStoryActivity.RESULT_CODE) {
            storyViewModel.getStory()
            storyViewModel.story?.observe(this) {
                setRecyclerView()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserPreference = UserPreference(this)
        userModel = mUserPreference.getUser()
        val token = "Bearer " + userModel.token.toString()

        storyViewModel.setToken(token)
        storyViewModel.getStory()

        setRecyclerView()

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.story_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.logout -> {
                userModel.token = null
                userModel.userId = null
                userModel.name = null
                mUserPreference.setUser(userModel)

                val moveToMainActivity = Intent(this, MainActivity::class.java)
                startActivity(moveToMainActivity)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val adapter = StoryListAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        storyViewModel.story?.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
    }
}