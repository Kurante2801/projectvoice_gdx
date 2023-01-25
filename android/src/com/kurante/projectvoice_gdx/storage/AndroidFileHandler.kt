package com.kurante.projectvoice_gdx.storage

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.ByteArrayOutputStream
import java.io.IOException

class AndroidFileHandler(
    private val document: DocumentFile,
    private val context: Context
) : FileHandler {
    override val path: String = document.uri.path!!
    override val encodedPath: String = document.uri.encodedPath!!

    override fun toString(): String = document.uri.toString()

    override fun getBytes(): ByteArray {
        context.contentResolver.openInputStream(document.uri).use { input ->
            if(input == null)
                throw IOException()

            val bytes = ByteArrayOutputStream()

            var len: Int
            while(input.read().also { len = it } > -1)
                bytes.write(len)

            return bytes.toByteArray()
        }
    }

    override fun getFiles(): Array<FileHandler> {
        val document = DocumentFile.fromTreeUri(context, document.uri)
        assert(document != null)

        return document!!.listFiles()
            .map { AndroidFileHandler(it, context) }.toTypedArray()
    }

}