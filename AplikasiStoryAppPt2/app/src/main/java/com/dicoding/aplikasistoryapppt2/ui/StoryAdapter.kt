package com.dicoding.aplikasistoryapppt2.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.databinding.StoryListBinding

class StoryAdapter(private val listStory: List<ItemStoryResponse>) : RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: StoryListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = listStory[position]

        holder.binding.tvItemName.text = story.name
        Glide
            .with(holder.itemView.context)
            .load(story.photoUrl)
            .fitCenter()
            .into(holder.binding.ivItemPhoto)

        holder.itemView.setOnClickListener {
            val moveToDetailStoryActivity = Intent(holder.itemView.context, DetailStoryActivity::class.java)
            moveToDetailStoryActivity.putExtra(DetailStoryActivity.EXTRA_ID, story.id)
            holder.itemView.context.startActivity(moveToDetailStoryActivity)
        }
    }
}