package com.abdallahshabat.cloudvault.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.core.managers.TokenManager
import com.abdallahshabat.cloudvault.databinding.FragmentProfileBinding
import com.abdallahshabat.cloudvault.ui.about.AboutActivity
import com.abdallahshabat.cloudvault.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.fragment.app.viewModels
import com.abdallahshabat.cloudvault.databinding.DialogChangePasswordBinding
import com.abdallahshabat.cloudvault.ui.help.HelpActivity
import com.abdallahshabat.cloudvault.ui.privacy.PrivacyActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.EmailAuthProvider

/*** ProfileFragment
 * يعرض بيانات المستخدم (الاسم، الإيميل، مساحة التخزين)
 * ويحتوي على خيارات الحساب: تعديل البروفايل، تغيير كلمة السر،
 * الإشعارات، الخصوصية، تسجيل الخروج.
 * Displays user profile info and account options.*/
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupClickListeners()
    }

    /*** يعبي بيانات المستخدم على الواجهة
     * Fills user data on the profile header + storage card.*/
    private fun setupUI() {
        val user = TokenManager.getUser(requireContext())

        binding.tvUserName.text = user?.fullName ?: "مستخدم"
        binding.tvUserEmail.text = user?.email ?: ""
        binding.tvUserInitial.text = user?.email?.first()?.uppercase() ?: "U"

        val used = user?.storageUsed ?: 0L
        val total = user?.storageTotal ?: 10L
        val percent = if (total > 0) ((used.toFloat() / total) * 100).toInt() else 0

        binding.storageProgress.progress = percent
        binding.tvStoragePercent.text = "$percent%"
        binding.tvStorageInfo.text = "${formatSize(used)} من ${formatSize(total)}"

        // TODO: اربطها مع ViewModel لجلب العدد الحقيقي للملفات والمشاركات
        binding.tvFilesCount.text = "0"
        binding.tvSharedCount.text = "0"
    }

    private fun setupClickListeners() {

        binding.rowEditProfile.setOnClickListener {
            // TODO: افتح شاشة/Dialog تعديل الاسم والصورة
        }

        binding.rowChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // TODO: احفظ تفضيل الإشعارات (SharedPreferences أو Firestore)
        }

        binding.rowPrivacy.setOnClickListener {
            startActivity(Intent(requireContext(), PrivacyActivity::class.java))
        }

        binding.rowHelp.setOnClickListener {
            startActivity(Intent(requireContext(), HelpActivity::class.java))

        }

        binding.rowAbout.setOnClickListener {
            /*عند الضغط على "عن التطبيق":
              نفتح AboutActivity (نفس نمط فتح LoginActivity بالأسفل)
              بدل Navigation Component لأنه غير مستخدم بهاد المشروع حاليًا.*/
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            binding.rowEditProfile.performClick()
        }
    }
    /*** يعرض Dialog لتغيير كلمة السر بدل فتح شاشة كاملة
     * نفس نهج renameFile(): Dialog مبني من layout مخصص عبر ViewBinding.
     * Shows a popup dialog to change the password instead of a full screen.*/
    private fun showChangePasswordDialog() {

        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogBinding.root)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSavePassword.setOnClickListener {
            attemptChangePassword(dialogBinding, dialog)
        }

        dialog.show()
    }
    private fun attemptChangePassword(
        dialogBinding: DialogChangePasswordBinding,
        dialog: androidx.appcompat.app.AlertDialog
    ) {
        val currentPassword = dialogBinding.etCurrentPassword.text.toString().trim()
        val newPassword = dialogBinding.etNewPassword.text.toString().trim()
        val confirmPassword = dialogBinding.etConfirmPassword.text.toString().trim()

        dialogBinding.tilCurrentPassword.error = null
        dialogBinding.tilNewPassword.error = null
        dialogBinding.tilConfirmPassword.error = null

        if (currentPassword.isEmpty()) {
            dialogBinding.tilCurrentPassword.error = "أدخل كلمة السر الحالية"
            return
        }

        if (newPassword.length < 6) {
            dialogBinding.tilNewPassword.error = "يجب أن تكون 6 أحرف على الأقل"
            return
        }

        if (newPassword != confirmPassword) {
            dialogBinding.tilConfirmPassword.error = "كلمتا السر غير متطابقتين"
            return
        }

        if (newPassword == currentPassword) {
            dialogBinding.tilNewPassword.error = "كلمة السر الجديدة يجب أن تختلف عن الحالية"
            return
        }

        changePassword(currentPassword, newPassword, dialogBinding, dialog)
    }
    /*** يعيد مصادقة المستخدم بكلمة السر الحالية أولًا (مطلوب من Firebase)
     * ثم يحدّث كلمة السر إذا نجحت إعادة المصادقة.
     * Re-authenticates with the current password, then updates to the new one.*/
    private fun changePassword(
        currentPassword: String,
        newPassword: String,
        dialogBinding: DialogChangePasswordBinding,
        dialog: androidx.appcompat.app.AlertDialog
    ) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email

        if (user == null || email == null) {
            Snackbar.make(binding.root, "تعذر التحقق من الجلسة، سجّل الدخول من جديد.", Snackbar.LENGTH_LONG).show()
            return
        }

        setDialogLoading(dialogBinding, true)

        val credential = EmailAuthProvider.getCredential(email, currentPassword)

        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener {
                        setDialogLoading(dialogBinding, false)
                        Snackbar.make(binding.root, "تم تغيير كلمة السر بنجاح.", Snackbar.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        setDialogLoading(dialogBinding, false)
                        Snackbar.make(
                            binding.root,
                            e.message ?: "فشل تحديث كلمة السر.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener {
                setDialogLoading(dialogBinding, false)
                dialogBinding.tilCurrentPassword.error = "كلمة السر الحالية غير صحيحة"
            }
    }
    private fun setDialogLoading(dialogBinding: DialogChangePasswordBinding, isLoading: Boolean) {
        dialogBinding.progressSave.visibility = if (isLoading) View.VISIBLE else View.GONE
        dialogBinding.tvSaveLabel.text = if (isLoading) "جارِ الحفظ..." else getString(
            com.abdallahshabat.cloudvault.R.string.save
        )
        dialogBinding.btnSavePassword.isEnabled = !isLoading
    }
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("تسجيل الخروج")
            .setMessage("هل أنت متأكد أنك تريد تسجيل الخروج؟")
            .setNegativeButton("إلغاء", null)
            .setPositiveButton("خروج") { _, _ ->
                /*SettingsFragment
                    │
                    ▼
              AuthViewModel
                   │
                   ▼
              AuthRepository
                  │
                  ▼
          FirebaseAuth.signOut()*/
                //يسجل خروج المستخدم من Firebase:
                FirebaseAuth.getInstance().signOut()
                //يحذف البيانات المحلية:
                TokenManager.clear(requireContext())
                //يفتح شاشة تسجيل الدخول:
                val intent = Intent(requireContext(), LoginActivity::class.java)
                //يمنع الرجوع إلى MainActivity بزر الرجوع باستخدام:
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)
                requireActivity().finish()
            }
            .show()
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
}
