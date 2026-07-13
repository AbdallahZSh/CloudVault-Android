package com.abdallahshabat.cloudvault.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.utils.ClipboardHelper
import com.abdallahshabat.cloudvault.core.utils.FileOpener
import com.abdallahshabat.cloudvault.core.utils.FileSharer
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.BottomSheetFileDetailsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.abdallahshabat.cloudvault.core.utils.TimeFormatter

class FileDetailsBottomSheet(
    private val file: CloudFile
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFileDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BottomSheetFileDetailsBinding.inflate(inflater, container, false)
        bindData()
        binding.btnOpen.setOnClickListener {
            FileOpener.open(requireContext(), file)
        }

        binding.btnShare.setOnClickListener {
            FileSharer.share(requireContext(), file)
        }

        binding.btnCopyLink.setOnClickListener {
            ClipboardHelper.copyLink(requireContext(), file)
        }

        return binding.root
    }
    private fun bindData() {
        binding.tvTitle.text = file.fileName

        binding.tvSubtitle.text = getFileTypeName(file)
        binding.ivFileType.setImageResource(

            when {

                file.fileType.startsWith("image") -> R.drawable.ic_image
                file.fileType.startsWith("video") -> R.drawable.ic_video
                file.fileType.startsWith("audio") -> R.drawable.ic_audio
                file.fileName.endsWith(".pdf", true) -> R.drawable.ic_pdf
                file.fileName.endsWith(".doc", true) || file.fileName.endsWith(".docx", true) -> R.drawable.ic_word
                file.fileName.endsWith(".xls", true) || file.fileName.endsWith(".xlsx", true) -> R.drawable.ic_excel
                file.fileName.endsWith(".ppt", true) || file.fileName.endsWith(".pptx", true) -> R.drawable.ic_ppt
                file.fileName.endsWith(".zip", true) -> R.drawable.ic_zip
                else -> R.drawable.ic_file
            }
        )
        binding.tvName.text = "📄 Name\n${file.fileName}"
        binding.tvSize.text = "📦 Size\n${formatFileSize(file.fileSize)}"
        binding.tvType.text = "🗂 Type\n${file.fileType}"
        binding.tvUploaded.text = "📅 Uploaded\n${TimeFormatter.getTimeAgo(file.uploadedAt)}"
        binding.tvFavorite.text = "⭐ Favorite\n${if (file.isFavorite) "Yes" else "No"}"
        binding.tvPublicId.text = "☁ Public ID\n${file.publicId}"
        binding.tvUrl.text = "🔗 URL\n${file.fileUrl}"
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> String.format("%.2f GB", size / 1024f / 1024f / 1024f)
            size >= 1024 * 1024 -> String.format("%.2f MB", size / 1024f / 1024f)
            size >= 1024 -> String.format("%.2f KB", size / 1024f)
            else -> "$size Bytes"

        }
    }
    private fun getFileTypeName(file: CloudFile): String {

        return when {

            file.fileType.startsWith("image") ->
                "Image"

            file.fileType.startsWith("video") ->
                "Video"

            file.fileType.startsWith("audio") ->
                "Audio"

            file.fileName.endsWith(".pdf", true) ->
                "PDF Document"

            file.fileName.endsWith(".doc", true) ||
                    file.fileName.endsWith(".docx", true) ->
                "Word Document"

            file.fileName.endsWith(".xls", true) ||
                    file.fileName.endsWith(".xlsx", true) ->
                "Excel Spreadsheet"

            file.fileName.endsWith(".ppt", true) ||
                    file.fileName.endsWith(".pptx", true) ->
                "PowerPoint Presentation"

            file.fileName.endsWith(".zip", true) ->
                "ZIP Archive"

            else ->
                "File"

        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}