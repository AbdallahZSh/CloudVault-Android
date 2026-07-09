package com.abdallahshabat.cloudvault.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.ItemFileBinding
import com.bumptech.glide.Glide

class FileAdapter :
    ListAdapter<CloudFile, FileAdapter.FileViewHolder>(DiffCallback()) {

    private var listener: OnFileClickListener? = null
    private var favoriteListener: ((CloudFile) -> Unit)? = null

    fun setOnFileClickListener(listener: OnFileClickListener) {
        this.listener = listener
    }

    fun setOnFavoriteClickListener(listener: (CloudFile) -> Unit) {
        favoriteListener = listener
    }

    interface OnFileClickListener {

        fun onFileClick(file: CloudFile)

        fun onMoreClick(
            file: CloudFile,
            anchorView: View
        )
    }

    inner class FileViewHolder(
        private val binding: ItemFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(file: CloudFile) {

            binding.tvFileName.text = file.fileName
            binding.tvFileType.text = file.fileType
            binding.tvFileSize.text = formatFileSize(file.fileSize)

            binding.root.setOnClickListener {
                listener?.onFileClick(file)
            }

            binding.btnMore.setOnClickListener {
                listener?.onMoreClick(file, binding.btnMore)
            }

//            binding.btnFavorite.setImageResource(
//                if (file.isFavorite)
//                    R.drawable.ic_favorite_border
//
//                else
//                    R.drawable.ic_favorite
//
//            )
//
//            binding.btnFavorite.setOnClickListener {
//                favoriteListener?.invoke(file)
//            }

            when {

                file.fileType.contains("image", true) -> {

                    Glide.with(binding.root)
                        .load(file.fileUrl)
                        .placeholder(R.drawable.ic_file)
                        .error(R.drawable.ic_file)
                        .centerCrop()
                        .into(binding.ivFileIcon)

                }

                file.fileType.contains("pdf", true) -> {

                    binding.ivFileIcon.setImageResource(R.drawable.ic_pdf)

                }

                else -> {

                    binding.ivFileIcon.setImageResource(R.drawable.ic_file)

                }

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileViewHolder {

        val binding = ItemFileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FileViewHolder,
        position: Int
    ) {

        holder.bind(getItem(position))

    }

    private fun formatFileSize(size: Long): String {

        return when {

            size >= 1024L * 1024L * 1024L ->
                String.format("%.2f GB", size / 1024f / 1024f / 1024f)

            size >= 1024L * 1024L ->
                String.format("%.2f MB", size / 1024f / 1024f)

            size >= 1024L ->
                String.format("%.2f KB", size / 1024f)

            else ->
                "$size Bytes"

        }

    }

    class DiffCallback : DiffUtil.ItemCallback<CloudFile>() {

        override fun areItemsTheSame(
            oldItem: CloudFile,
            newItem: CloudFile
        ): Boolean {

            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(
            oldItem: CloudFile,
            newItem: CloudFile
        ): Boolean {

            return oldItem == newItem

        }

    }

}