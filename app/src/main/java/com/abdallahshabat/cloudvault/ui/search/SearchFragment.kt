package com.abdallahshabat.cloudvault.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.core.utils.FileOpener
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.FragmentSearchBinding
import com.abdallahshabat.cloudvault.ui.home.adapter.FileAdapter
import android.widget.PopupMenu
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.managers.SearchHistoryManager

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()

    private lateinit var adapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentSearchBinding.inflate(
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
        observeResults()
        setupSearch()
        setupFilters()

        binding.searchBar.setOnLongClickListener {
            showSortMenu()
            true
        }


    }

    private fun setupRecycler() {

        adapter = FileAdapter()

        adapter.setOnFileClickListener(

            object : FileAdapter.OnFileClickListener {

                override fun onFileClick(file: CloudFile) {

                    FileOpener.open(
                        requireContext(),
                        file
                    )

                }

                override fun onMoreClick(file: CloudFile, anchorView: View) {
                    // سنضيف Popup Menu لاحقًا
                }

            }

        )

        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext())

        binding.rvSearch.adapter = adapter

    }

    private fun observeResults() {

        viewModel.results.observe(viewLifecycleOwner) { files ->

            adapter.submitList(files)

            binding.tvEmpty.visibility =
                if (files.isEmpty())
                    View.VISIBLE
                else
                    View.GONE

        }

    }

    private fun setupSearch() {

        binding.searchBar.doOnTextChanged { text, _, _, _ ->

            viewModel.search(
                text.toString()
            )
            SearchHistoryManager.save(

                requireContext(),

                text.toString()

            )
        }
    }
    private fun setupFilters() {

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            if (checkedIds.isEmpty())
                return@setOnCheckedStateChangeListener

            when (checkedIds.first()) {

                binding.chipAll.id ->
                    viewModel.setFilter("ALL")

                binding.chipImages.id ->
                    viewModel.setFilter("IMAGE")

                binding.chipVideos.id ->
                    viewModel.setFilter("VIDEO")

                binding.chipAudio.id ->
                    viewModel.setFilter("AUDIO")

                binding.chipDocuments.id ->
                    viewModel.setFilter("DOCUMENT")

            }

            viewModel.search(
                binding.searchBar.text.toString()
            )

        }

    }

    private fun showSortMenu() {

        val popup = PopupMenu(

            requireContext(),

            binding.searchBar

        )

        popup.menuInflater.inflate(

            R.menu.search_sort_menu,

            popup.menu

        )

        popup.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.sortNewest ->
                    viewModel.setSort("NEWEST")

                R.id.sortOldest ->
                    viewModel.setSort("OLDEST")

                R.id.sortLargest ->
                    viewModel.setSort("LARGEST")

                R.id.sortSmallest ->
                    viewModel.setSort("SMALLEST")

                R.id.sortAZ ->
                    viewModel.setSort("AZ")

                R.id.sortZA ->
                    viewModel.setSort("ZA")

            }

            true

        }

        popup.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}