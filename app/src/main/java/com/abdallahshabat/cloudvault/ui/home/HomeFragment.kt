package com.abdallahshabat.cloudvault.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.utils.ClipboardHelper
import com.abdallahshabat.cloudvault.core.utils.FileOpener
import com.abdallahshabat.cloudvault.core.utils.FileDownloader
import com.abdallahshabat.cloudvault.core.utils.FileSharer
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.databinding.DialogRenameFileBinding
import com.abdallahshabat.cloudvault.databinding.FragmentHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.abdallahshabat.cloudvault.ui.home.adapter.FileAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.fragment.findNavController
import com.abdallahshabat.cloudvault.ui.details.FileDetailsBottomSheet
import com.abdallahshabat.cloudvault.ui.notifications.NotificationViewModel


/*HomeFragment
      │
      ▼
setupRecyclerView()
      │
      ▼
observeFiles()
      │
      ▼
viewModel.loadFiles()
      │
      ▼
Repository
      │
      ▼
Firestore
      │
      ▼
List<CloudFile>
      │
      ▼
FileAdapter.submitList()
      │
      ▼
RecyclerView*/
class HomeFragment : Fragment() {
    /** RecyclerView Adapter.
     * Responsible for displaying uploaded files.
     * مسؤول عن عرض الملفات داخل RecyclerView.*/
    private lateinit var fileAdapter: FileAdapter

    /*** Home ViewModel.*/
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        observeFiles()
        observeDeleteState()
        observeRenameState()
        observeFavoriteState()
        readANDunReadNotification()
        viewModel.loadFiles()
    }
    private fun readANDunReadNotification() {
        notificationViewModel.loadUnreadCount()

        notificationViewModel.unreadCount.observe(viewLifecycleOwner) { count ->

            if (count > 0) {
                binding.tvNotificationBadge.visibility = View.VISIBLE

                if (count > 99)
                    binding.tvNotificationBadge.text = "99+"
                else
                    binding.tvNotificationBadge.text = count.toString()

            } else {
                binding.tvNotificationBadge.visibility = View.GONE
            }

        }
    }
    private fun setupUI() {
        val user = TokenManager.getUser(requireContext())
        binding.tvUserName.text = user?.fullName ?: "مرحباً"
        binding.tvUserInitial.text = user?.email?.first()?.toString() ?: "أ"

        // Storage bar
        val used = user?.storageUsed ?: 0L
        val total = user?.storageTotal ?: 10L
        val percent = ((used.toFloat() / total) * 100).toInt()
        binding.storageProgress.progress = percent
        binding.tvStorageInfo.text = "${formatSize(used)} من ${formatSize(total)}"

        // Quick actions
        binding.cardUpload.setOnClickListener {
            parentFragmentManager.let {
                this.requireActivity().findViewById<BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.uploadFragment
            }
        }
        binding.cardShare.setOnClickListener {
            shareApp()
        }
        binding.cardFavorites.setOnClickListener {
            findNavController().navigate(R.id.favoritesFragment)
        }
        binding.cardNewFolder.setOnClickListener {
            findNavController().navigate(R.id.filesFragment)
        }
        binding.ivSearch.setOnClickListener {

            findNavController().navigate(
                R.id.searchFragment
            )

        }
        binding.btnNotification.setOnClickListener {

            findNavController().navigate(R.id.notificationFragment)

            notificationViewModel.markAllAsRead()

        }
    }


    private fun formatSize(bytes: Long): String {
        return when {
            bytes >= 1_073_741_824 -> "%.1f GB".format(bytes / 1_073_741_824.0)
            bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
            else -> "%.1f KB".format(bytes / 1_024.0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*** Initializes RecyclerView.
     * Creates Adapter and assigns LayoutManager.
     * تهيئة RecyclerView وربط الـ Adapter.*/
    private fun setupRecyclerView() {
        /*ثم يصبح setupRecyclerView()
         FileAdapter
            ↓
         Listener
            ↓
         RecyclerView*/
        fileAdapter = FileAdapter()

        fileAdapter.setOnFileClickListener(
            object : FileAdapter.OnFileClickListener {
                override fun onFileClick(file: CloudFile) {
                    FileOpener.open(
                        requireContext(),
                        file
                    )
                }

                override fun onMoreClick(file: CloudFile, anchorView: View) {
                    showFileOptionsMenu(file, anchorView)
                }
            }
        )
        fileAdapter.setOnFavoriteClickListener { file ->
            viewModel.toggleFavorite(file)
        }

        binding.rvRecentFiles.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = fileAdapter
        }

    }

    /*** Observes uploaded files.
     * Whenever HomeViewModel updates the file list,
     * RecyclerView is refreshed automatically.
     * مراقبة قائمة الملفات.
     * عند وصول بيانات جديدة يتم تحديث RecyclerView.*/
    private fun observeFiles() {

        viewModel.recentFiles.observe(viewLifecycleOwner) { files ->

            fileAdapter.submitList(files)

            binding.tvEmpty.visibility =
                if (files.isEmpty()) View.VISIBLE else View.GONE

            val usedStorage = files.sumOf { it.fileSize }

            val totalStorage = 10L * 1024 * 1024 * 1024L // 10 GB

            binding.tvStorageInfo.text =
                "${formatSize(usedStorage)} من ${formatSize(totalStorage)}"

            binding.storageProgress.progress =
                ((usedStorage.toFloat() / totalStorage) * 100).toInt()

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

        popupMenu.menuInflater.inflate(
            R.menu.file_options_menu,
            popupMenu.menu
        )

        popupMenu.menu.findItem(R.id.action_favorite).title =
            if (file.isFavorite)
                "Remove from Favorites"
            else
                "Add to Favorites"

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_open -> {
                    FileOpener.open(requireContext(), file)
                    true
                }

                R.id.action_download -> {
                    FileDownloader.download(requireContext(), file)
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

                R.id.action_rename -> {
                    renameFile(file)
                    true
                }

                R.id.action_details -> {

                    FileDetailsBottomSheet(file)
                        .show(
                            parentFragmentManager,
                            "file_details"
                        )

                    true
                }

                R.id.action_delete -> {
                    showDeleteDialog(file)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    /*** مشاركة التطبيق نفسه مع الأصدقاء
     * عبر أي تطبيق مثبت (واتساب، تيليجرام، ايميل...)
     * Share the app itself via any installed app */
    private fun shareApp() {
        val appPackageName = requireContext().packageName
        val shareText = """
        جرب تطبيق CloudVault لتخزين ومشاركة ملفاتك بسهولة! ☁️
        حمّله من هنا:
        https://play.google.com/store/apps/details?id=$appPackageName
    """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(
            android.content.Intent.createChooser(
                shareIntent,
                getString(R.string.share_app)
            )
        )
    }

    private fun showDeleteDialog(file: CloudFile) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete File")
            .setMessage("Are you sure you want to delete\n\n${file.fileName} ?\n\nThis action cannot be undone.")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.setPositiveButton("Delete") { _, _ ->
                viewModel.deleteFile(file)
            }
            .show()
    }

    /**
     * Observes delete result.
     * Refreshes RecyclerView after deleting a file.
     * مراقبة نتيجة حذف الملف.
     */
    private fun observeDeleteState() {
        viewModel.deleteState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    viewModel.loadFiles()
                    Snackbar.make(binding.root, "File deleted successfully.", Snackbar.LENGTH_SHORT)
                        .show()
                },
                onFailure = {
                    Snackbar.make(binding.root, "Failed to delete file.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    //مراقبة
    private fun observeFavoriteState() {
        viewModel.favoriteState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.favorite_updated_successfully),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.loadFiles()
                },
                onFailure = {
                    Snackbar.make(
                        binding.root,
                        it.message ?: getString(R.string.failed_update_favorite),
                        Snackbar.LENGTH_SHORT
                    ).show()
                    viewModel.loadFiles()
                }
            )
        }
    }

    //راقب النتيجة تعت اعادة التسمية
    private fun observeRenameState() {
        viewModel.renameState.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = {
                    viewModel.loadFiles()
                    Snackbar.make(binding.root, "File renamed successfully.", Snackbar.LENGTH_SHORT)
                        .show()
                },
                onFailure = {
                    Snackbar.make(binding.root, "Failed to rename file.", Snackbar.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    private fun renameFile(file: CloudFile) {

        val dialogBinding = DialogRenameFileBinding.inflate(layoutInflater)

        dialogBinding.etFileName.setText(file.fileName)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnRename.setOnClickListener {
            val newName = dialogBinding.etFileName.text.toString().trim()
            when {
                newName.isEmpty() -> {
                    dialogBinding.etFileName.error = "Required"
                }

                newName == file.fileName -> {
                    dialog.dismiss()
                }

                else -> {
                    viewModel.renameFile(file, newName)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }
}