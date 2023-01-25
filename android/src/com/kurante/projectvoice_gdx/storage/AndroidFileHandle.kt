package com.kurante.projectvoice_gdx.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.Files
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import java.io.*

class AndroidFileHandle(
    private val context: Context,
    private val document: DocumentFile
) : FileHandle() {
    override fun path(): String = document.uri.path!!
    override fun name(): String = document.name!!
    override fun type(): Files.FileType = Files.FileType.Absolute
    override fun toString(): String = document.uri.path!!
    override fun isDirectory(): Boolean = document.isDirectory
    override fun exists(): Boolean = document.exists()
    override fun delete(): Boolean = document.delete()

    override fun read(): InputStream {
        if(!document.exists())
            throw GdxRuntimeException("(Android) File not found: $document")
        if(document.isDirectory)
            throw GdxRuntimeException("(Android) Cannot open a stream to a directory: $document")
        if(document.canRead())
            throw GdxRuntimeException("(Android) Cannot read file: $document")

        return try {
            context.contentResolver.openInputStream(document.uri)!!
        } catch (e: Exception) {
            throw GdxRuntimeException("(Android) Error reading file: $document", e)
        }
    }

    override fun readBytes(): ByteArray {
        read().use {  input ->
            val output = ByteArrayOutputStream()
            input.copyTo(output)
            return output.toByteArray()
        }
    }

    override fun readString(): String {
        read().use { stream ->
            BufferedReader(InputStreamReader(stream)).use {
                return it.readText()
            }
        }
    }

    override fun write(append: Boolean): OutputStream {
        if(document.isDirectory)
            throw GdxRuntimeException("(Android) Cannot open a stream to a directory: ${document.uri.path}")
        val output = context.contentResolver.openOutputStream(document.uri)
            ?: throw GdxRuntimeException("(Android) Error opening output stream: ${document.uri.path}")

        if(append && exists()) {
            context.contentResolver.openInputStream(document.uri).use { input ->
                input?.copyTo(output)
            }
        }

        return output
    }

    override fun list(): Array<FileHandle> {
        // To deal with SAF's weird handling of directories,
        // we have to first read children and determine if they're directories
        // If they're directories we need to re parse them as trees (as opposed to singleUri for files)
        // This assumes the first uri we've ever gotten was a document tree
        return document.listFiles().map { child(document.name!!) }.toTypedArray()
    }

    override fun child(name: String): AndroidFileHandle {
        var doc = DocumentFile.fromSingleUri(
            context,
            Uri.parse(document.uri.toString() + Uri.encode("/$name"))
        ) ?: throw GdxRuntimeException("(Android) Failed to get a document from DocumentFile.fromSingleUri: child($name)")

        // In case of a directory, convert to tree
        if(doc.isDirectory)
            doc = DocumentFile.fromTreeUri(context, doc.uri)
                ?: throw GdxRuntimeException("(Android) Failed to get a document from DocumentFile.fromTreeUri: child($name)")

        return AndroidFileHandle(context, doc)
    }

    override fun parent() = AndroidFileHandle(context, document.parentFile!!)

    override fun copyTo(dest: FileHandle) {
        if(!isDirectory)
            copyFile(dest)
        else
            copyDirectory(dest)
    }

    private fun copyFile(dest: AndroidFileHandle) {
        read().use { input ->
            context.contentResolver.openOutputStream(dest.document.uri).use { output ->
                if(output == null)
                    throw GdxRuntimeException("(Android) Failed to get an OutputStream from destination: $dest")
                input.copyTo(output)
            }
        }
    }

    private fun copyFile(dest: FileHandle) {
        if(dest is AndroidFileHandle)
            copyFile(dest)
        else
            dest.write(read(), false)
    }

    private fun copyDirectory(dest: AndroidFileHandle) {
        TODO("NOT IMPLEMENTED IN ANDROID")
    }

    private fun copyDirectory(dest: FileHandle) {
        if(dest is AndroidFileHandle)
            copyDirectory(dest)
        else
            TODO("NOT IMPLEMENTED IN ANDROID")
    }


}