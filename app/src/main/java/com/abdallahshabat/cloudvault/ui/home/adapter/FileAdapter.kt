package com.abdallahshabat.cloudvault.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.ItemFileBinding
import com.bumptech.glide.Glide

/**
 * ------------------------------------------------------------
 * File Name : FileAdapter.kt
 * Module    : Home
 * Project   : CloudVault
 *
 * English:
 * RecyclerView Adapter responsible for displaying
 * uploaded files inside HomeFragment.
 *
 * Responsibilities:
 * - Creates ViewHolder objects.
 * - Binds CloudFile data to each row.
 * - Updates RecyclerView when data changes.
 *
 * This adapter does NOT communicate with Firestore.
 * It only displays the data received from HomeViewModel.
 * هذا الملف مسؤول عن عرض الملفات داخل RecyclerView.
 * مسؤولياته:
 * - إنشاء ViewHolder.
 * - ربط بيانات الملف مع الواجهة.
 * - تحديث القائمة عند تغير البيانات.
 * هذا الملف لا يتعامل مع Firestore نهائياً،
 * وإنما يعرض البيانات القادمة من HomeViewModel.
 */
class FileAdapter :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    /**
     * Internal list containing all uploaded files.
     * RecyclerView reads data from this list.
     * هذه هي القائمة التي يعتمد عليها RecyclerView
     * في عرض الملفات.*/
    private val files = mutableListOf<CloudFile>()

    /*** Receives click events from RecyclerView items.*/
    private var listener: OnFileClickListener? = null

    /*** Registers click listener.*/
    fun setOnFileClickListener(
        listener: OnFileClickListener
    ) {
        this.listener = listener
    }
    /**
     * Listener used to notify HomeFragment about user actions.
     * This adapter does not perform any business logic.
     * It only reports click events.
     * واجهة لإبلاغ HomeFragment بالأحداث التي يقوم بها المستخدم.
     * هذا الـ Adapter لا ينفذ أي عملية حذف أو فتح،
     * وإنما يبلغ الـ Fragment فقط بما حدث.
     */
    interface OnFileClickListener {
        /*** Called when the user clicks the file item.*/
        fun onFileClick(file: CloudFile)
        /*** Called when the user presses the More button.*/
        fun onMoreClick(file: CloudFile, anchorView: View)
    }
    /**
     * ViewHolder
     * Holds references to the views of one item.
     * يحتفظ بمراجع عناصر واجهة الملف الواحد
     * حتى لا يتم البحث عنها كل مرة.
     */
    inner class FileViewHolder(
        private val binding: ItemFileBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /*** Binds one CloudFile object to UI.
         * ربط بيانات ملف واحد مع عناصر الواجهة.*/
        fun bind(file: CloudFile) {
            binding.tvFileName.text = file.fileName
            binding.tvFileType.text = file.fileType
            binding.tvFileSize.text = formatFileSize(file.fileSize)
            /*** File click.*/
            binding.root.setOnClickListener {
                listener?.onFileClick(file)
            }
            /*** More button click.*/
            binding.btnMore.setOnClickListener {

                listener?.onMoreClick(file, binding.btnMore)

            }
            /*** Change icon depending on file type.*/
            when {
                    file.fileType.contains("image", true) -> {
                        Glide.with(binding.root)
                            .load(file.fileUrl)
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

    /**
     * Creates new ViewHolder.
     */
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

    /**
     * Binds data to ViewHolder.
     */
    override fun onBindViewHolder(
        holder: FileViewHolder,
        position: Int
    ) {

        holder.bind(files[position])

    }

    /**
     * Number of displayed items.
     */
    override fun getItemCount(): Int {

        return files.size

    }

    /**
     * Updates RecyclerView with new data.
     *
     * العربية:
     * استبدال القائمة الحالية بقائمة جديدة
     * ثم تحديث RecyclerView.
     */
    fun submitList(
        newFiles: List<CloudFile>
    ) {

        files.clear()

        files.addAll(newFiles)

        notifyDataSetChanged()

    }

    /**
     * Formats file size.
     */
    private fun formatFileSize(
        size: Long
    ): String {

        return when {

            size >= 1024 * 1024 * 1024 ->
                String.format("%.2f GB", size / 1024f / 1024f / 1024f)

            size >= 1024 * 1024 ->
                String.format("%.2f MB", size / 1024f / 1024f)

            size >= 1024 ->
                String.format("%.2f KB", size / 1024f)

            else ->
                "$size Bytes"
        }

    }

}