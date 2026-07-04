package com.abdallahshabat.cloudvault.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
        viewModel.loadFiles()
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
                requireActivity().findViewById<BottomNavigationView>(
                    R.id.bottomNav
                ).selectedItemId = R.id.uploadFragment
            }
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
        }
    }

    private fun showFileOptionsMenu(file: CloudFile, anchor: View) {

        val popupMenu = PopupMenu(requireContext(), anchor)

        popupMenu.menuInflater.inflate(
            R.menu.file_options_menu,
            popupMenu.menu
        )

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {

                R.id.action_open -> {
                    /*الآن أصبحت ميزة Open تعمل من مكانين
الضغط على الملف نفسه.
اختيار Open من قائمة الخيارات.

وكلاهما يستخدم نفس الكود داخل  FileOpener                    .    */
                    FileOpener.open(
                        requireContext(),
                        file
                    )

                    true
                }

                R.id.action_download -> {
                    FileDownloader.download(
                        requireContext(),
                        file
                    )

                    true
                }

                R.id.action_share -> {
                    FileSharer.share(
                        requireContext(),
                        file
                    )

                    true
                }

                R.id.action_rename -> {
                  true
                }

                R.id.action_copy_link -> {
                    //عند الضغط على Copy Link:
                    //✅ يتم نسخ رابط Cloudinary.
                    // تظهر رسالة:
                    //Link copied to clipboard.
                    ClipboardHelper.copyLink(
                        requireContext(),
                        file
                    )

                    true
                }

                R.id.action_delete -> {
                    /*ماذا سيحدث الآن؟
إذا ضغط المستخدم Delete...
                    لن يحذف مباشرة.
                    بل سيظهر Dialog احترافي.
وعند الضغط على Delete...
سنستدعي
                     viewModel.deleteFile(file)
الخطوة التالية (بعد أن تنتهي من هذه)
سنذهب إلى HomeViewModel ونضيف
                    fun deleteFile(...)
ثم نربطها مع Repository.
ثم نجعل RecyclerView يحدث نفسه مباشرة.
ملاحظة مهمة جدًا
                    لاحظ أننا لا نكتب:
                    repository.deleteFile(...)
داخل الـ Fragment.
ولا نكتب:
                        FirebaseFirestore...
داخل الـ Fragment.
لأن هذا يكسر MVVM.
                        سيبقى الـ Fragment مسؤولًا عن واجهة المستخدم فقط، بينما منطق الحذف سيكون في الـ ViewModel ثم الـ Repository.*/
                    showDeleteDialog(file)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    private fun showDeleteDialog(
        file: CloudFile
    ) {

        MaterialAlertDialogBuilder(requireContext())

            .setTitle("Delete File")

            .setMessage(
                "Are you sure you want to delete\n\n${file.fileName} ?\n\nThis action cannot be undone."
            )

            .setNegativeButton("Cancel", null)

            .setPositiveButton("Delete") { _, _ ->

                viewModel.deleteFile(file)

            }

            .show()

    }

    /**
     * ------------------------------------------------------------
     * Observes delete result.
     *
     * English:
     * Refreshes RecyclerView after deleting a file.
     *
     * العربية:
     * مراقبة نتيجة حذف الملف.
     * ------------------------------------------------------------
     */
    private fun observeDeleteState() {

        viewModel.deleteState.observe(viewLifecycleOwner) { result ->

            result.fold(

                onSuccess = {

                    viewModel.loadFiles()

                    Snackbar.make(
                        binding.root,
                        "File deleted successfully.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                },

                onFailure = {

                    Snackbar.make(
                        binding.root,
                        "Failed to delete file.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                }

            )

        }

    }
    private fun showRenameDialog(
        file: CloudFile
    ) {

        val editText =
            com.google.android.material.textfield.TextInputEditText(
                requireContext()
            )

        editText.setText(file.fileName)

        MaterialAlertDialogBuilder(requireContext())

            .setTitle("Rename File")

            .setView(editText)

            .setNegativeButton("Cancel", null)

            .setPositiveButton("Save") { _, _ ->

                val newName =
                    editText.text
                        ?.toString()
                        ?.trim()
                        ?: ""

                if (newName.isNotEmpty()) {

                    viewModel.renameFile(
                        file,
                        newName
                    )

                }

            }

            .show()

    }
    //راقب النتيجة تعت اعادة التسمية
    private fun observeRenameState() {

        viewModel.renameState.observe(
            viewLifecycleOwner
        ) { result ->

            result.fold(

                onSuccess = {

                    viewModel.loadFiles()

                    Snackbar.make(
                        binding.root,
                        "File renamed successfully.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                },

                onFailure = {

                    Snackbar.make(
                        binding.root,
                        "Failed to rename file.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                }

            )

        }

    }
    private fun renameFile(file: CloudFile) {

        val dialogBinding =
            DialogRenameFileBinding.inflate(layoutInflater)

        dialogBinding.etFileName.setText(file.fileName)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnRename.setOnClickListener {

            val newName =
                dialogBinding.etFileName.text.toString().trim()

            if (newName.isNotEmpty()) {

                viewModel.renameFile(file, newName)

                dialog.dismiss()
            }
        }

        dialog.show()
    }

    //إضافة Download function داخل Fragment
    private fun downloadFile(file: CloudFile) {

        val request = android.app.DownloadManager.Request(android.net.Uri.parse(file.fileUrl))
            .setTitle(file.fileName)
            .setDescription("Downloading file...")
            .setNotificationVisibility(
                android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = requireContext().getSystemService(android.content.Context.DOWNLOAD_SERVICE)
                as android.app.DownloadManager

        dm.enqueue(request)

        Snackbar.make(binding.root, "Downloading started...", Snackbar.LENGTH_SHORT).show()
    }


    //إضافة Open File function
    private fun openFile(file: CloudFile) {

        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)

        intent.setDataAndType(
            android.net.Uri.parse(file.fileUrl),
            getMimeType(file.fileType)
        )

        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Snackbar.make(binding.root, "No app found to open this file", Snackbar.LENGTH_SHORT).show()
        }
    }


    //دالة تحديد نوع الملف (MIME Type)
    private fun getMimeType(type: String): String {

        return when (type.lowercase()) {

            "pdf" -> "application/pdf"
            "png" -> "image/png"
            "jpg", "jpeg" -> "image/jpeg"
            "mp4" -> "video/mp4"
            "mp3" -> "audio/mpeg"

            else -> "*/*"
        }
    }

    //المستخدم يضغط Share
    //
    //→ يفتح تطبيقات الجهاز:
    //
    //WhatsApp
    //Telegram
    //Gmail
    //Messenger
    //✔ يتم إرسال:
    //اسم الملف
    //رابط Cloudinary

    private fun shareFile(file: CloudFile) {

        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND)

        shareIntent.type = "text/plain"

        val shareText = """
        📁 File: ${file.fileName}

        🔗 Link:
        ${file.fileUrl}

        🚀 Shared via CloudVault
    """.trimIndent()


        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            android.content.Intent.EXTRA_TEXT,
            file.fileUrl
        )

        startActivity(
            android.content.Intent.createChooser(
                shareIntent,
                "Share file via"
            )
        )
    }
}