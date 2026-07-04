package com.abdallahshabat.cloudvault.data.remote.upload

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File

class ProgressRequestBody(
    private val file: File,
    private val contentType: MediaType?,
    private val listener: UploadProgressListener
) : RequestBody() {

    override fun contentType(): MediaType? = contentType

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {

        val totalBytes = file.length()
        var uploadedBytes = 0L

        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        file.inputStream().use { inputStream ->

            var read: Int

            while (inputStream.read(buffer).also { read = it } != -1) {

                uploadedBytes += read

                sink.write(buffer, 0, read)

                val progress = ((uploadedBytes * 100) / totalBytes).toInt()

                listener.onProgressUpdate(progress)
            }
        }
    }
}