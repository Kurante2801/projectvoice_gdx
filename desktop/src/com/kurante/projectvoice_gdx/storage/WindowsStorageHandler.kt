package com.kurante.projectvoice_gdx.storage

import com.badlogic.gdx.files.FileHandle
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.async.newSingleThreadAsyncContext
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.memFree
import org.lwjgl.util.nfd.NativeFileDialog

class WindowsStorageHandler : StorageHandler {
    // https://github.com/spookygames/gdx-nativefilechooser/blob/d0d6bb3b0ee91e174865493a953c69216c8c22f3/desktop-lwjgl/src/main/java/games/spooky/gdx/nativefilechooser/desktop/DesktopFileChooser.java#L68
    override fun requestFolderAccess(callback: (FileHandler?) -> Unit) {
        val executor = newSingleThreadAsyncContext()
        KtxAsync.launch(executor) {
            openFolderDialog(callback)
        }
    }

    private fun openFolderDialog(callback: (FileHandler?) -> Unit) {
        val path = MemoryUtil.memAllocPointer(1)
        callback.invoke(
            try {
                when (NativeFileDialog.NFD_PickFolder(null as CharSequence?, path)) {
                    NativeFileDialog.NFD_OKAY -> WindowsFileHandler(FileHandle(path.stringUTF8))
                    NativeFileDialog.NFD_CANCEL -> null
                    NativeFileDialog.NFD_ERROR -> throw Exception(NativeFileDialog.NFD_GetError())
                    else -> null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                memFree(path)
            }
        )
    }

}