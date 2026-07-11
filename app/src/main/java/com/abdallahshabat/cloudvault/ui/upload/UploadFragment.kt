package com.abdallahshabat.cloudvault.ui.upload

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abdallahshabat.cloudvault.databinding.FragmentUploadBinding
import androidx.fragment.app.viewModels
import android.app.Activity
import android.content.Intent
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.abdallahshabat.cloudvault.core.utils.NotificationHelper
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class UploadFragment : Fragment() {
    private var _binding: FragmentUploadBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UploadViewModel by viewModels()
    private var selectedFileUri: Uri? = null
    private var selectedFileName = ""
    private var selectedFileType = ""
    private var selectedFileSize = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUploadBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data ?: return@registerForActivityResult

                selectedFileUri = uri

                requireContext().contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )?.use { cursor ->

                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                    cursor.moveToFirst()
                    selectedFileName = cursor.getString(nameIndex)
                    selectedFileSize = cursor.getLong(sizeIndex)
                }

                selectedFileType = requireContext().contentResolver.getType(uri) ?: "Unknown"

                binding.tvFileName.text = selectedFileName
                binding.tvFileSize.text = "Size: ${formatFileSize(selectedFileSize)}"

                binding.btnUpload.isEnabled = true
            }
        }
    private fun chooseFile() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(intent)
    }
    private fun formatFileSize(size: Long): String {

        return when {
            size >= 1024 * 1024 -> String.format("%.2f MB", size / 1024f / 1024f)
            size >= 1024 -> String.format("%.2f KB", size / 1024f)
            else -> "$size Bytes"
        }

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uploadProgress.observe(viewLifecycleOwner) { progress ->

            binding.uploadProgress.visibility = View.VISIBLE
            binding.tvProgress.visibility = View.VISIBLE

            binding.uploadProgress.progress = progress
            binding.tvProgress.text = "$progress%"

        }
        binding.btnChooseFile.setOnClickListener {
            chooseFile()
        }

        binding.btnUpload.setOnClickListener {

            val uri = selectedFileUri ?: return@setOnClickListener

            viewModel.uploadFile(
                context = requireContext(),
                userId = FirebaseAuth.getInstance().currentUser!!.uid,
                fileName = selectedFileName,
                fileUri = uri,
                fileType = selectedFileType,
                fileSize = selectedFileSize
            )
        }

        observeState()
    }
    private fun observeState() {

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is UploadState.Idle -> {
                    binding.progressBar.visibility = View.GONE
                }

                is UploadState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.uploadProgress.visibility = View.VISIBLE
                    binding.tvProgress.visibility = View.VISIBLE
                }

                is UploadState.Success -> {

                    binding.progressBar.visibility = View.GONE
                    binding.uploadProgress.visibility = View.GONE
                    binding.tvProgress.visibility = View.GONE

                    binding.uploadProgress.progress = 0
                    binding.tvProgress.text = "0%"

                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@observe

                    NotificationHelper.addNotification(userId, "Upload Completed", "${state.file.fileName} uploaded successfully", "upload")

                    Snackbar.make(binding.root, "File uploaded successfully", Snackbar.LENGTH_SHORT).show()

                    binding.tvFileName.text = "No file selected"
                    binding.tvFileSize.text = "Size : -"

                    binding.btnUpload.isEnabled = false

                    selectedFileUri = null
                    selectedFileName = ""
                    selectedFileType = ""
                    selectedFileSize = 0L

                    viewModel.resetState()
                }

                is UploadState.Error -> {

                    binding.progressBar.visibility = View.GONE
                    binding.uploadProgress.visibility = View.GONE
                    binding.tvProgress.visibility = View.GONE

                    binding.uploadProgress.progress = 0
                    binding.tvProgress.text = "0%"

                    Toast.makeText(
                        requireContext(),
                        state.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}