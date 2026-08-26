package com.github.lucky.clash.design

import android.content.Context
import android.net.Uri
import android.view.View
import com.github.lucky.clash.design.databinding.DesignSettingsCommonBinding
import com.github.lucky.clash.design.preference.clickable
import com.github.lucky.clash.design.preference.preferenceScreen
import com.github.lucky.clash.design.preference.tips
import com.github.lucky.clash.design.util.applyFrom
import com.github.lucky.clash.design.util.bindAppBarElevation
import com.github.lucky.clash.design.util.layoutInflater
import com.github.lucky.clash.design.util.root

class HelpDesign(
    context: Context,
    openLink: (Uri) -> Unit,
) : Design<Unit>(context) {
    private val binding = DesignSettingsCommonBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    init {
        binding.surface = surface

        binding.activityBarLayout.applyFrom(context)

        binding.scrollRoot.bindAppBarElevation(binding.activityBarLayout)

        val screen = preferenceScreen(context) {
            tips(R.string.tips_help)

            clickable(
                title = R.string.google,
                summary = R.string.google_url
            ) {
                clicked {
                    openLink(Uri.parse(context.getString(R.string.google_url)))
                }
            }
        }

        binding.content.addView(screen.root)
    }
}