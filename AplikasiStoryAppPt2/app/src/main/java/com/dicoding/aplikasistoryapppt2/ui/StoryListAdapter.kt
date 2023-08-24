package com.dicoding.aplikasistoryapppt2.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.aplikasistoryapppt2.data.ItemStoryResponse
import com.dicoding.aplikasistoryapppt2.databinding.StoryListBinding

class StoryListAdapter :
    PagingDataAdapter<ItemStoryResponse, StoryListAdapter.MyViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    inner class MyViewHolder(private val binding: StoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(data: ItemStoryResponse) {
                binding.tvItemName.text = data.name
                Glide
                    .with(binding.root)
                    .load(data.photoUrl)
                    .fitCenter()
                    .into(binding.ivItemPhoto)
                binding.root.setOnClickListener {
                    val moveToDetailStoryActivity = Intent(binding.root.context, DetailStoryActivity::class.java)
                    moveToDetailStoryActivity.putExtra(DetailStoryActivity.EXTRA_ID, data.id)
                    binding.root.context.startActivity(moveToDetailStoryActivity)
                }
            }
        }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ItemStoryResponse>() {
            override fun areItemsTheSame(
                oldItem: ItemStoryResponse,
                newItem: ItemStoryResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ItemStoryResponse,
                newItem: ItemStoryResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }
}