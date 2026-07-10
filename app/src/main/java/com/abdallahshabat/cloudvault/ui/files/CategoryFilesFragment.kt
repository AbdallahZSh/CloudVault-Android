package com.abdallahshabat.cloudvault.ui.files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.utils.ClipboardHelper
import com.abdallahshabat.cloudvault.core.utils.FileDownloader
import com.abdallahshabat.cloudvault.core.utils.FileOpener
import com.abdallahshabat.cloudvault.core.utils.FileSharer
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.FragmentCategoryFilesBinding
import com.abdallahshabat.cloudvault.ui.home.adapter.FileAdapter

class CategoryFilesFragment : Fragment() {

    private var _binding: FragmentCategoryFilesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoryFilesViewModel by viewModels()

    private lateinit var fileAdapter: FileAdapter

    private val args: CategoryFilesFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCategoryFilesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvCategoryTitle.text =
            if (args.category.startsWith("DOC_"))
                args.category.removePrefix("DOC_")
            else
                args.category

        setupRecycler()

        observeFiles()
        observeFavoriteState()

        viewModel.loadFiles(args.category)
    }

    private fun setupRecycler() {

        fileAdapter = FileAdapter()

        fileAdapter.setOnFavoriteClickListener {

            viewModel.toggleFavorite(it)

        }

        fileAdapter.setOnFileClickListener(
            object : FileAdapter.OnFileClickListener {

                override fun onFileClick(file: CloudFile) {
                    FileOpener.open(requireContext(), file)
                }

                override fun onMoreClick(file: CloudFile, anchorView: View) {
                    showMenu(file, anchorView)
                }

            }
        )

        binding.rvFiles.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFiles.adapter = fileAdapter
    }

    private fun observeFiles() {

        viewModel.files.observe(viewLifecycleOwner) {

            binding.tvCount.text = "${it.size} Files"

            fileAdapter.submitList(it)

        }

    }
    private fun observeFavoriteState() {

        viewModel.favoriteState.observe(viewLifecycleOwner) {

            viewModel.loadFiles(args.category)

        }

    }

    private fun showMenu(file: CloudFile, anchor: View) {

        val popup = PopupMenu(
            requireContext(),
            anchor,
            0,
            0,
            R.style.CloudVault_PopupMenu
        )

        popup.menuInflater.inflate(
            R.menu.file_options_menu,
            popup.menu
        )

        popup.menu.findItem(R.id.action_favorite).title =
            if (file.isFavorite)
                "Remove from Favorites"
            else
                "Add to Favorites"

        popup.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.action_open -> {

                    FileOpener.open(requireContext(), file)

                    true
                }

                R.id.action_download -> {

                    FileDownloader.download(requireContext(), file)

                    true
                }
                R.id.action_favorite -> {

                    viewModel.toggleFavorite(file)

                    true

                }

                R.id.action_share -> {

                    FileSharer.share(requireContext(), file)

                    true
                }

                R.id.action_copy_link -> {

                    ClipboardHelper.copyLink(requireContext(), file)

                    true
                }

                else -> false
            }

        }

        popup.show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}