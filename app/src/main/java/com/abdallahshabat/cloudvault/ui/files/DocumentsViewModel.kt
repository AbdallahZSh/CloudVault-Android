package com.abdallahshabat.cloudvault.ui.files

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdallahshabat.cloudvault.R
import com.abdallahshabat.cloudvault.data.model.DocumentType
import com.abdallahshabat.cloudvault.data.repository.FileRepository
import com.abdallahshabat.cloudvault.data.repository.FileRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class DocumentsViewModel : ViewModel() {

    private val repository: FileRepository = FileRepositoryImpl()

    private val _categories = MutableLiveData<List<DocumentType>>()
    val categories: LiveData<List<DocumentType>> = _categories

    fun loadDocuments() {

        val userId =
            FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {

            repository.getFiles(userId).fold(

                onSuccess = { files ->

                    val documents = files.filter {

                        !it.fileType.startsWith("image") &&
                                !it.fileType.startsWith("video") &&
                                !it.fileType.startsWith("audio")

                    }

                    val pdfFiles = documents.filter {
                        it.fileType.contains("pdf", true)
                    }

                    val wordFiles = documents.filter {
                        it.fileName.endsWith(".doc", true) ||
                                it.fileName.endsWith(".docx", true)
                    }

                    val excelFiles = documents.filter {
                        it.fileName.endsWith(".xls", true) ||
                                it.fileName.endsWith(".xlsx", true)
                    }

                    val pptFiles = documents.filter {
                        it.fileName.endsWith(".ppt", true) ||
                                it.fileName.endsWith(".pptx", true)
                    }

                    val zipFiles = documents.filter {
                        it.fileName.endsWith(".zip", true)
                    }

                    val textFiles = documents.filter {
                        it.fileName.endsWith(".txt", true)
                    }

                    _categories.value = listOf(

                        DocumentType(
                            title = "PDF",
                            icon = R.drawable.ic_pdf,
                            count = pdfFiles.size,
                            totalSize = pdfFiles.sumOf { it.fileSize }
                        ),

                        DocumentType(
                            title = "Word",
                            icon = R.drawable.ic_word,
                            count = wordFiles.size,
                            totalSize = wordFiles.sumOf { it.fileSize }
                        ),

                        DocumentType(
                            title = "Excel",
                            icon = R.drawable.ic_excel,
                            count = excelFiles.size,
                            totalSize = excelFiles.sumOf { it.fileSize }
                        ),

                        DocumentType(
                            title = "PowerPoint",
                            icon = R.drawable.ic_ppt,
                            count = pptFiles.size,
                            totalSize = pptFiles.sumOf { it.fileSize }
                        ),

                        DocumentType(
                            title = "ZIP",
                            icon = R.drawable.ic_zip,
                            count = zipFiles.size,
                            totalSize = zipFiles.sumOf { it.fileSize }
                        ),

                        DocumentType(
                            title = "Text",
                            icon = R.drawable.ic_text,
                            count = textFiles.size,
                            totalSize = textFiles.sumOf { it.fileSize }
                        )

                    )

                },

                onFailure = {

                    _categories.value = emptyList()

                }

            )

        }

    }

}