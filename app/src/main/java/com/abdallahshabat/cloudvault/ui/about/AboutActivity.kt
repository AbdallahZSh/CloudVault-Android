package com.abdallahshabat.cloudvault.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abdallahshabat.cloudvault.databinding.ActivityAboutBinding
import com.google.firebase.firestore.BuildConfig

/*** AboutActivity
 * شاشة "عن التطبيق": تعرض شعار CloudVault، نسخة التطبيق، وصف مختصر،
 * أهم الميزات، وروابط الشروط/الخصوصية/التواصل.
 * تُفتح من ProfileFragment عبر Intent (نفس نمط LoginActivity).*/
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupClickListeners()
    }

    private fun setupUI() {
        // بيعرض رقم نسخة التطبيق تلقائيًا من build.gradle (versionName)
        binding.tvAppVersion.text = "الإصدار ${BuildConfig.VERSION_NAME}"
    }

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.rowTerms.setOnClickListener {
            openLink("https://cloudvault.app/terms")
        }

        binding.rowPrivacyPolicy.setOnClickListener {
            openLink("https://cloudvault.app/privacy")
        }

        binding.rowContactUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@cloudvault.app")
                putExtra(Intent.EXTRA_SUBJECT, "دعم CloudVault")
            }
            startActivity(Intent.createChooser(intent, "تواصل معنا عبر"))
        }
    }

    private fun openLink(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
