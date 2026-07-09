package com.abdallahshabat.cloudvault.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.utils.ClipboardHelper
import com.abdallahshabat.cloudvault.core.utils.FileDownloader
import com.abdallahshabat.cloudvault.core.utils.FileOpener
import com.abdallahshabat.cloudvault.core.utils.FileSharer
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.abdallahshabat.cloudvault.databinding.FragmentFavoritesBinding
import com.abdallahshabat.cloudvault.ui.home.adapter.FileAdapter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels

class FavoritesFragment : Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var fileAdapter: FileAdapter

    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoritesBinding.inflate(
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

        setupRecyclerView()

        observeFavorites()
        observeFavoriteFiles()
        viewModel.loadFavorites()
    }
    private fun setupRecyclerView() {

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
                    showFileOptionsMenu(file, anchorView)
                }
            }
        )

        binding.rvFavorites.apply {

            layoutManager = LinearLayoutManager(requireContext())

            adapter = fileAdapter

        }
    }
    private fun showFileOptionsMenu(file: CloudFile, anchor: View) {

        val popupMenu = PopupMenu(
            requireContext(),
            anchor,
            0,
            0,
            R.style.CloudVault_PopupMenu
        )
        popupMenu.menuInflater.inflate(R.menu.file_options_menu, popupMenu.menu)

        val favoriteItem = popupMenu.menu.findItem(R.id.action_favorite)

        favoriteItem.title =
            if (file.isFavorite)
                "إزالة من المفضلة"
            else
                "إضافة إلى المفضلة"

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_open -> {
                    FileOpener.open(requireContext(), file)
                    true
                }
                R.id.action_download -> { FileDownloader.download(requireContext(), file)
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

                R.id.action_favorite -> {
                    viewModel.toggleFavorite(file)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }
    private fun observeFavorites() {

            viewModel.favoriteFiles.observe(viewLifecycleOwner) { files ->

                for (file in files) {
                    android.util.Log.d(
                        "Fav",
                        "${file.fileName}  favorite=${file.isFavorite}"
                    )
                }

                fileAdapter.submitList(files)
            }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun observeFavoriteFiles() {

        viewModel.favoriteFiles.observe(viewLifecycleOwner) { files ->

            fileAdapter.submitList(files)

        }

    }
}