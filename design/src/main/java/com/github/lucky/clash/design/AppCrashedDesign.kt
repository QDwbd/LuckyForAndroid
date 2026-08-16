package com.github.lucky.clash.design

import android.content.Context
import android.view.View
import com.github.lucky.clash.design.databinding.DesignAppCrashedBinding
import com.github.lucky.clash.design.util.applyFrom
import com.github.lucky.clash.design.util.bindAppBarElevation
import com.github.lucky.clash.design.util.layoutInflater
import com.github.lucky.clash.design.util.root

class AppCrashedDesign(context: Context) : Design<Unit>(context) {
    private val binding = DesignAppCrashedBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    fun setAppLogs(logs: String) {
        binding.logsView.text = logs
    }

    init {
        binding.self = this

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)
    }
}