package com.abdallahshabat.cloudvault.ui.files.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abdallahshabat.cloudvault.data.model.FileCategory
import com.abdallahshabat.cloudvault.databinding.ItemFileCategoryBinding

class CategoryAdapter :
    ListAdapter<FileCategory, CategoryAdapter.CategoryViewHolder>(DiffCallback()) {

    private var listener: ((FileCategory) -> Unit)? = null

    fun setOnCategoryClickListener(listener: (FileCategory) -> Unit) {
        this.listener = listener
    }

    inner class CategoryViewHolder(
        private val binding: ItemFileCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: FileCategory) {

            binding.ivIcon.setImageResource(category.icon)

            binding.tvTitle.text = category.title

            binding.tvCount.text = "${category.count} Files"

            binding.root.setOnClickListener {
                listener?.invoke(category)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {

        val binding = ItemFileCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FileCategory>() {

        override fun areItemsTheSame(
            oldItem: FileCategory,
            newItem: FileCategory
        ) = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: FileCategory,
            newItem: FileCategory
        ) = oldItem == newItem
    }
}