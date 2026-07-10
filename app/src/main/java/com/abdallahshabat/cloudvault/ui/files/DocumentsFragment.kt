package com.abdallahshabat.cloudvault.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.databinding.FragmentDocumentsBinding
import com.abdallahshabat.cloudvault.ui.files.adapter.DocumentTypeAdapter

class DocumentsFragment : Fragment() {

    private var _binding: FragmentDocumentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DocumentsViewModel by viewModels()

    private lateinit var adapter: DocumentTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentDocumentsBinding.inflate(
                inflater,
                container,
                false
            )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        observeCategories()

        viewModel.loadDocuments()

    }

    private fun setupRecycler() {

        adapter = DocumentTypeAdapter()

        binding.rvDocuments.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvDocuments.adapter = adapter

        adapter.setOnDocumentClickListener { category ->

            val action =
                DocumentsFragmentDirections
                    .actionDocumentsFragmentToCategoryFilesFragment(
                        "DOC_${category.title}"
                    )

            findNavController().navigate(action)

        }

    }

    private fun observeCategories() {

        viewModel.categories.observe(viewLifecycleOwner) {

            adapter.submitList(it)

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}