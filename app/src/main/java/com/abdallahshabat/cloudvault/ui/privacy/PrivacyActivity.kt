package com.abdallahshabat.cloudvault.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abdallahshabat.cloudvault.databinding.ActivityPrivacyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/*** PrivacyActivity
 * شاشة "الخصوصية والأمان": قفل التطبيق (بصمة/PIN)، التحقق بخطوتين،
 * الجلسات النشطة، تحميل بياناتي، سياسة الخصوصية، وحذف الحساب.
 * تُفتح من ProfileFragment عبر Intent.*/
class PrivacyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // TODO: اقرأ القيم الفعلية المحفوظة (SharedPreferences) بدل القيم الافتراضية
        binding.switchAppLock.isChecked = false
        binding.switchTwoFactor.isChecked = false
    }

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.switchAppLock.setOnCheckedChangeListener { _, isChecked ->
            // TODO: فعّل/عطّل قفل التطبيق بالبصمة أو PIN واحفظ التفضيل
        }

        binding.switchTwoFactor.setOnCheckedChangeListener { _, isChecked ->
            // TODO: فعّل/عطّل التحقق بخطوتين عبر Firebase Auth (Phone/Email verification)
        }

        binding.rowActiveSessions.setOnClickListener {
            // TODO: افتح شاشة تعرض الأجهزة/الجلسات المسجلة دخول حاليًا
        }

        binding.rowDownloadData.setOnClickListener {
            // TODO: صدّر بيانات المستخدم (JSON/CSV) وأرسلها بالإيميل أو حمّلها محليًا
            Snackbar.make(binding.root, "سيتم إرسال بياناتك خلال 24 ساعة إلى بريدك.", Snackbar.LENGTH_LONG).show()
        }

        binding.rowPrivacyPolicy.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://cloudvault.app/privacy")))
        }

        binding.rowDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }
    }

    private fun showDeleteAccountDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("حذف الحساب")
            .setMessage("سيتم حذف حسابك وجميع ملفاتك نهائيًا. هذا الإجراء لا يمكن التراجع عنه.")
            .setNegativeButton("إلغاء", null)
            .setPositiveButton("حذف نهائي") { _, _ ->
                deleteAccount()
            }
            .show()
    }

    /*** يحذف حساب المستخدم من Firebase Auth
     * قد يتطلب إعادة تسجيل الدخول (reauthenticate) إذا مرّ وقت طويل على آخر جلسة.
     * Deletes the Firebase Auth account; may require re-authentication.*/
    private fun deleteAccount() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnSuccessListener {
                // TODO: احذف بيانات المستخدم من Firestore + Storage أيضًا
                // TODO: امسح TokenManager وانتقل لـ LoginActivity
                Snackbar.make(binding.root, "تم حذف الحساب بنجاح.", Snackbar.LENGTH_SHORT).show()
            }
            ?.addOnFailureListener {
                Snackbar.make(
                    binding.root,
                    "فشل حذف الحساب. قد تحتاج لتسجيل الدخول من جديد ثم إعادة المحاولة.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }
}
