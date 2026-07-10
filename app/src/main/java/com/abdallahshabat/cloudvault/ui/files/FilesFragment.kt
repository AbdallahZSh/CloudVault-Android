package com.abdallahshabat.cloudvault.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.databinding.FragmentFilesBinding
import com.abdallahshabat.cloudvault.ui.files.adapter.CategoryAdapter
import androidx.navigation.fragment.findNavController
class FilesFragment : Fragment() {

    private var _binding: FragmentFilesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilesViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFilesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        observeCategories()
        observeStorage()
        viewModel.loadFiles()

    }

    private fun setupRecycler() {

        categoryAdapter = CategoryAdapter()

        binding.rvCategories.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = categoryAdapter

        }
        categoryAdapter.setOnCategoryClickListener { category ->

            when (category.title) {

                "Documents" -> {

                    val action =
                        FilesFragmentDirections
                            .actionFilesFragmentToDocumentsFragment()

                    findNavController().navigate(action)

                }

                else -> {

                    val action =
                        FilesFragmentDirections
                            .actionFilesFragmentToCategoryFilesFragment(category.title)

                    findNavController().navigate(action)

                }

            }

        }

    }

    private fun observeCategories() {

        viewModel.categories.observe(viewLifecycleOwner) {

            categoryAdapter.submitList(it)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun observeStorage() {

        viewModel.totalFiles.observe(viewLifecycleOwner) {

            binding.tvStorageInfo.text = "$it Files"

        }

        viewModel.totalSize.observe(viewLifecycleOwner) {

            binding.tvStorageSize.text =
                "${formatSize(it)} Used"

            val percent =
                ((it.toFloat() / (10L * 1024 * 1024 * 1024)) * 100)
                    .toInt()
                    .coerceAtMost(100)

            binding.progressStorage.progress = percent

        }

    }
    private fun formatSize(size: Long): String {

        return when {

            size >= 1024L * 1024L * 1024L ->
                String.format("%.2f GB", size / 1024f / 1024f / 1024f)

            size >= 1024L * 1024L ->
                String.format("%.2f MB", size / 1024f / 1024f)

            size >= 1024L ->
                String.format("%.2f KB", size / 1024f)

            else ->
                "$size B"

        }

    }
}