package com.github.lucky.clash.design

import android.content.Context
import android.view.View
import com.github.lucky.clash.design.databinding.DesignFileViewerBinding
import com.github.lucky.clash.design.util.applyFrom
import com.github.lucky.clash.design.util.layoutInflater
import com.github.lucky.clash.design.util.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileViewerDesign(context: Context) : Design<FileViewerDesign.Request>(context) {
    enum class Request {
        Close
    }

    private val binding = DesignFileViewerBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    suspend fun patchContent(content: String?) {
        withContext(Dispatchers.Main) {
            binding.textView.text = content ?: ""
        }
    }

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)
    }
}
