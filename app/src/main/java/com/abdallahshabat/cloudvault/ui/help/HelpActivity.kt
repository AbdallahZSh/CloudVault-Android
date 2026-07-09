package com.abdallahshabat.cloudvault.ui.help

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.abdallahshabat.cloudvault.databinding.ActivityHelpBinding
import com.google.firebase.firestore.BuildConfig

/*** HelpActivity
 * شاشة "المساعدة والدعم": تواصل سريع (إيميل/واتساب)، أسئلة شائعة قابلة للطي،
 * وزر للإبلاغ عن مشكلة يرسل إيميل فيه معلومات الجهاز والنسخة تلقائيًا.
 * تُفتح من ProfileFragment عبر Intent (نفس نمط AboutActivity).*/
class HelpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpBinding

    private val supportEmail = "support@cloudvault.app"
    private val whatsappNumber = "970000000000" // TODO: بدّله برقمك الحقيقي بصيغة دولية بدون +

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.cardEmailSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$supportEmail")
                putExtra(Intent.EXTRA_SUBJECT, "دعم CloudVault")
            }
            startActivity(Intent.createChooser(intent, "تواصل عبر"))
        }

        binding.cardWhatsappSupport.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$whatsappNumber")
                }
                startActivity(intent)
            } catch (e: Exception) {
                // TODO: أظهر رسالة إذا ما في تطبيق واتساب مثبت
            }
        }

        binding.rowFaq1.setOnClickListener {
            toggleFaq(binding.tvFaq1Answer, binding.ivFaq1Arrow)
        }

        binding.rowFaq2.setOnClickListener {
            toggleFaq(binding.tvFaq2Answer, binding.ivFaq2Arrow)
        }

        binding.rowFaq3.setOnClickListener {
            toggleFaq(binding.tvFaq3Answer, binding.ivFaq3Arrow)
        }

        binding.rowFaq4.setOnClickListener {
            toggleFaq(binding.tvFaq4Answer, binding.ivFaq4Arrow)
        }

        binding.rowReportProblem.setOnClickListener {
            reportProblem()
        }
    }

    /*** يفتح/يقفل إجابة سؤال شائع مع تدوير السهم
     * Expands/collapses a FAQ answer and rotates its arrow.*/
    private fun toggleFaq(answerView: View, arrowView: View) {
        val isVisible = answerView.visibility == View.VISIBLE
        answerView.visibility = if (isVisible) View.GONE else View.VISIBLE
        arrowView.animate().rotation(if (isVisible) 0f else 90f).setDuration(150).start()
    }

    /*** يفتح تطبيق الإيميل مع تفاصيل الجهاز والنسخة معبّاة تلقائيًا
     * Opens email client pre-filled with device + app version info.*/
    private fun reportProblem() {
        val deviceInfo = """

            ---
            نسخة التطبيق: ${BuildConfig.VERSION_NAME}
            جهاز: ${Build.MANUFACTURER} ${Build.MODEL}
            إصدار أندرويد: ${Build.VERSION.RELEASE}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$supportEmail")
            putExtra(Intent.EXTRA_SUBJECT, "إبلاغ عن مشكلة - CloudVault")
            putExtra(Intent.EXTRA_TEXT, "اكتب وصف المشكلة هنا:\n\n\n$deviceInfo")
        }
        startActivity(Intent.createChooser(intent, "أرسل التقرير عبر"))
    }
}
