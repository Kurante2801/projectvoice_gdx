package com.kurante.projectvoice_gdx.storage

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.Files
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.GdxRuntimeException
import com.kurante.projectvoice_gdx.util.extensions.random
import java.io.*

class AndroidFileHandle(
    private val context: Context,
    private val document: DocumentFile
) : FileHandle() {
    private val fileChars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray()
    private fun randomFileName(): String {
        val filename = charArrayOf('0', '1', '2', '3', '4', '5')

        for(i in 0..5)
            filename[i] = fileChars.random()

        return String(filename)
    }

    fun createDirectory(name: String): AndroidFileHandle? {
        val created = document.createDirectory(name)
            ?: return null
        return AndroidFileHandle(context, created)
    }

    fun createFile(name: String): AndroidFileHandle? {
        // Due to SAF, we can't create any file with any arbitrary name
        // so instead we create an empty txt file and rename it to what we want
        var tempFile = randomFileName()
        while(document.findFile("$tempFile.txt") != null)
            tempFile = randomFileName()

        val created = document.createFile("text/plain", "$tempFile.txt")
            ?: return null

        if(!created.renameTo(name)) {
            created.delete()
            return null
        }

        return AndroidFileHandle(context, created)
    }

    override fun path(): String = document.uri.path!!
    override fun name(): String = document.name!!

    override fun type(): Files.FileType = Files.FileType.Absolute
    override fun toString(): String = document.uri.toString()
    override fun isDirectory(): Boolean = document.isDirectory
    override fun exists(): Boolean = document.exists()
    override fun delete(): Boolean = document.delete()

    override fun extension(): String {
        val name = name()
        val i = name.lastIndexOf('.')
        return if(i == -1) "" else name.substring(i + 1)
    }

    override fun nameWithoutExtension(): String {
        val name = name()
        val i = name.lastIndexOf('.')
        return if(i == -1) name else name.substring(0, i)
    }

    override fun pathWithoutExtension(): String {
        val path = path()
        val i = path.lastIndexOf(".")
        return if(i == -1) path else path.substring(0, i)
    }

    override fun read(): InputStream {
        if(!document.exists())
            throw GdxRuntimeException("(Android) File not found: $document")
        if(document.isDirectory)
            throw GdxRuntimeException("(Android) Cannot open a stream to a directory: $document")
        if(!document.canRead())
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
        val children = mutableListOf<DocumentFile>()

        for(doc in document.listFiles()) {
            if(!doc.exists() || doc.name == null) continue
            children.add(doc)
        }

        return children.map { AndroidFileHandle(context, it) }.toTypedArray()
    }

    // Quite slow...
    override fun child(name: String): AndroidFileHandle {
        val doc = document.findFile(name) ?: DocumentFile.fromSingleUri(context, Uri.EMPTY)!!
        return AndroidFileHandle(context, doc)
    }

    override fun parent(): AndroidFileHandle {
        val parent = document.parentFile ?: DocumentFile.fromSingleUri(context, Uri.EMPTY)!!
        return AndroidFileHandle(context, parent)
    }

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