package com.abdallahshabat.cloudvault.ui.files.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abdallahshabat.cloudvault.data.model.DocumentType
import com.abdallahshabat.cloudvault.databinding.ItemDocumentTypeBinding

class DocumentTypeAdapter :
    ListAdapter<DocumentType, DocumentTypeAdapter.ViewHolder>(DiffCallback()) {

    private var listener: ((DocumentType) -> Unit)? = null

    fun setOnDocumentClickListener(listener: (DocumentType) -> Unit) {
        this.listener = listener
    }

    inner class ViewHolder(
        private val binding: ItemDocumentTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DocumentType) {

            binding.ivIcon.setImageResource(item.icon)

            binding.tvTitle.text = item.title

            binding.tvCount.text = "${item.count} files"

            binding.root.setOnClickListener {

                listener?.invoke(item)

            }

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemDocumentTypeBinding.inflate(

            LayoutInflater.from(parent.context),

            parent,

            false

        )

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))

    }

    class DiffCallback  : DiffUtil.ItemCallback<DocumentType>() {

        override fun areItemsTheSame(
            oldItem: DocumentType,
            newItem: DocumentType
        ) = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: DocumentType,
            newItem: DocumentType
        ) = oldItem == newItem

    }

}