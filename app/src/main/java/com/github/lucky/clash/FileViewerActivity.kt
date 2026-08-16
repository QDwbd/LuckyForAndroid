package com.github.lucky.clash

import android.net.Uri
import com.github.lucky.clash.design.FileViewerDesign
import com.github.lucky.clash.design.util.showExceptionToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class FileViewerActivity : BaseActivity<FileViewerDesign>() {
    override suspend fun main() {
        val uri = intent.data ?: return finish()

        val design = FileViewerDesign(this)

        setContentDesign(design)

        try {
            val content = withContext(Dispatchers.IO) { loadContent(uri) }

            design.patchContent(content)
        } catch (e: Exception) {
            design.showExceptionToast(e)

            return finish()
        }

        while (isActive) {
            select<Unit> {
                events.onReceive {

                }
                design.requests.onReceive {
                    when (it) {
                        FileViewerDesign.Request.Close -> finish()
                    }
                }
            }
        }
    }

    private fun loadContent(uri: Uri): String? {
        return contentResolver.openInputStream(uri)?.use { input ->
            input.bufferedReader().readText()
        }
    }
}
