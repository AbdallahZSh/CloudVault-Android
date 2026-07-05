package com.abdallahshabat.cloudvault.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.abdallahshabat.cloudvault.data.model.CloudFile
/*لماذا لم أضع الكود داخل HomeFragment؟

لأن HomeFragment مسؤول عن الواجهة فقط.

أما:

فتح الملفات
مشاركة الملفات
نسخ الرابط

فهذه كلها Utilities وليست منطق واجهة.

وهذا يجعل المشروع أنظف وأسهل للصيانة.

بعد أن تنشئ FileOpener.kt

سنكتب داخله المنطق الكامل بحيث يدعم:

✅ الصور.
✅ PDF.
✅ Word.
✅ Excel.
✅ PowerPoint.
✅ Text.
✅ Audio.
✅ Video.
✅ أي نوع ملفات آخر.

من أول مرة، بحيث لا نحتاج إلى تعديله عند إضافة أنواع ملفات جديدة. وهذا سيجعل ميزة Open مكتملة واحترافية.*/


/**
 * ------------------------------------------------------------
 * File Name : FileOpener.kt
 * Module    : Core
 * Project   : CloudVault
 *
 * English:
 * Utility class responsible for opening uploaded files.
 *
 * العربية:
 * كلاس مساعد مسؤول عن فتح الملفات المرفوعة.
 * ------------------------------------------------------------
 */
object FileOpener {

    /**
     * Opens the selected file.
     *
     * English:
     * Opens the file using the appropriate application
     * installed on the user's device.
     *
     * العربية:
     * فتح الملف باستخدام التطبيق المناسب
     * الموجود على جهاز المستخدم.
     */
    fun open(
        context: Context,
        file: CloudFile
    ) {

        val uri = Uri.parse(file.fileUrl)

        val intent = Intent(Intent.ACTION_VIEW).apply {

            data = uri

            setDataAndType(uri, file.fileType)

            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //لماذا أضفنا
            //Intent.FLAG_GRANT_READ_URI_PERMISSION
            //حالياً لن تؤثر كثيرًا لأن الملفات من Cloudinary.
            //لكن إذا انتقلت مستقبلاً إلى:
            //Firebase Storage
            //FileProvider
            //ملفات محلية
            //فلن تحتاج لتعديل الكود.
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        }

        try {

            context.startActivity(intent)

        } catch (e: ActivityNotFoundException) {

            Toast.makeText(
                context,
                "Cannot open ${file.fileName}",
                Toast.LENGTH_LONG
            ).show()

        }

    }

}