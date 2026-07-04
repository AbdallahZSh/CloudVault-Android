package com.abdallahshabat.cloudvault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.data.model.CloudFile
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * ------------------------------------------------------------
 * File Name : HomeViewModel.kt
 * Module    : Home
 * Project   : CloudVault
 *
 * English:
 * ViewModel responsible for the Home screen.
 *
 * Responsibilities:
 * - Requests the current user's files from the Repository.
 * - Holds the list of files using LiveData.
 * - Survives configuration changes (rotation, etc.).
 * - Separates UI from business logic (MVVM).
 *
 * This ViewModel never communicates directly with
 * Firestore or Cloudinary.
 * It only communicates with the Repository layer.
 *
 * العربية:
 * هذا الـ ViewModel مسؤول عن شاشة الصفحة الرئيسية.
 *
 * مسؤولياته:
 * - طلب ملفات المستخدم الحالية من Repository.
 * - الاحتفاظ بقائمة الملفات باستخدام LiveData.
 * - المحافظة على البيانات عند دوران الشاشة.
 * - فصل واجهة المستخدم عن منطق التطبيق وفق MVVM.
 *
 * هذا الملف لا يتعامل مباشرة مع Firestore أو Cloudinary،
 * وإنما يعتمد فقط على Repository.
 * ------------------------------------------------------------
 */
class HomeViewModel : ViewModel() {

    /**
     * Repository instance.
     * Acts as the bridge between ViewModel
     * and the data layer.
     * حلقة الوصل بين ViewModel وطبقة البيانات.*/

    private val repository: FileRepository = FileRepositoryImpl()

    /*** Internal mutable list of files.
     * MutableLiveData is private so only this ViewModel
     * can modify its value.
     * متغير داخلي قابل للتعديل ولا يمكن لأي كلاس
     * خارجي تغيير قيمته.*/
    private val _recentFiles =
        MutableLiveData<List<CloudFile>>()

    /**
     * Public immutable LiveData.
     * Fragments observe this variable
     * but cannot modify it.
     * الواجهة (Fragment) تستطيع مراقبة البيانات
     * لكنها لا تستطيع تعديلها.*/
    val recentFiles: LiveData<List<CloudFile>>
        get() = _recentFiles

    /**
     * Delete operation state.
     *
     * English:
     * Used to notify the Fragment when a file
     * has been deleted successfully or failed.
     *
     * العربية:
     * تستخدم لإبلاغ الواجهة بنجاح أو فشل عملية الحذف.
     */
    private val _deleteState =
        MutableLiveData<Result<CloudFile>>()

    val deleteState: LiveData<Result<CloudFile>>
        get() = _deleteState
    private val _renameState =
        MutableLiveData<Result<Unit>>()

    val renameState: LiveData<Result<Unit>>
        get() = _renameState

    /**
     * ------------------------------------------------------------
     * Renames a file.
     *
     * العربية:
     * تحديث اسم الملف.
     * ------------------------------------------------------------
     */
    fun renameFile(
        file: CloudFile,
        newName: String
    ) {

        viewModelScope.launch {

            repository
                .renameFile(file, newName)
                .fold(

                    onSuccess = {

                        _renameState.value =
                            Result.success(Unit)

                    },

                    onFailure = {

                        _renameState.value =
                            Result.failure(it)

                    }

                )

        }

    }
    /**
     * Loads all files of the currently logged-in user.
     *
     * Flow:
     * 1- Get current Firebase user.
     * 2- Read user's files from Repository.
     * 3- Repository fetches them from Firestore.
     * 4- Update LiveData.

     * تحميل جميع ملفات المستخدم الحالي.
     *
     * التسلسل:
     * ١- الحصول على المستخدم الحالي.
     * ٢- طلب الملفات من Repository.
     * ٣- يقوم Repository بجلبها من Firestore.
     * ٤- تحديث LiveData.
     */
    fun loadFiles() {

        /**
         * Get current user's UID.
         *
         * If no user is logged in,
         * there is nothing to load.
         *
         * الحصول على UID للمستخدم الحالي.
         * إذا لم يكن هناك مستخدم مسجل دخول
         * يتم إنهاء العملية.
         */
        val userId =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid
                ?: return

        /**
         * Launch coroutine.
         *
         * English:
         * Firestore operations are asynchronous.
         * Therefore we execute them inside
         * viewModelScope.
         *
         * العربية:
         * عمليات Firestore غير متزامنة،
         * لذلك يتم تشغيلها داخل Coroutine.
         */
        viewModelScope.launch {

            repository
                .getFiles(userId)
                .fold(

                    /**
                     * Success:
                     * Update UI with received files.
                     *
                     * نجاح العملية.
                     * تحديث القائمة بالملفات.
                     */
                    onSuccess = {

                        _recentFiles.value = it

                    },

                    /**
                     * Failure:
                     * Show empty list.
                     *
                     * عند حدوث خطأ
                     * نقوم بإرجاع قائمة فارغة.
                     *
                     * لاحقاً يمكن تحسين ذلك
                     * بإظهار رسالة للمستخدم.
                     */
                    onFailure = {

                        _recentFiles.value = emptyList()

                    }

                )
        }
    }

    /*هذا الملف هو العقل (Brain) لشاشة HomeFragment.
    بمعنى:
    HomeFragment
          │
          ▼
    HomeViewModel
          │
          ▼
    FileRepository
          │
          ▼
    Firestore
    فالـ Fragment لا يعرف شيئًا عن Firestore أو Cloudinary، وإنما يقول فقط:
    "أعطني ملفات المستخدم."
    والـ ViewModel يتولى هذه المهمة ثم يعيد النتائج إلى الواجهة عبر LiveData.*/


    /**
     * ------------------------------------------------------------
     * Deletes a file.
     *
     * English:
     * Requests Repository to delete the file.
     *
     * العربية:
     * طلب حذف الملف من Repository.
     * ------------------------------------------------------------
     */
    fun deleteFile(file: CloudFile) {
        viewModelScope.launch {
            repository.deleteFile(file).fold(
                onSuccess = {
                    _deleteState.value = Result.success(file)
                },
                onFailure = {
                    _deleteState.value = Result.failure(it)
                }
            )
        }
    }
}