package com.github.lucky.clash.design

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.github.lucky.clash.core.model.TunnelState
import com.github.lucky.clash.core.util.trafficTotal
import com.github.lucky.clash.design.databinding.ComponentConnectionBinding
import com.github.lucky.clash.design.databinding.DesignAboutBinding
import com.github.lucky.clash.design.databinding.DesignMainBinding
import com.github.lucky.clash.design.model.ConnectionEntry
import com.github.lucky.clash.design.util.layoutInflater
import com.github.lucky.clash.design.util.resolveThemedColor
import com.github.lucky.clash.design.util.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainDesign(context: Context) : Design<MainDesign.Request>(context) {
    enum class Request {
        ToggleStatus,
        OpenProxy,
        OpenProfiles,
        OpenProviders,
        OpenLogs,
        OpenSettings,
        OpenHelp,
        OpenAbout,
    }

    private val binding = DesignMainBinding
        .inflate(context.layoutInflater, context.root, false)

    override val root: View
        get() = binding.root

    suspend fun setProfileName(name: String?) {
        withContext(Dispatchers.Main) {
            binding.profileName = name
        }
    }

    suspend fun setClashRunning(running: Boolean) {
        withContext(Dispatchers.Main) {
            binding.clashRunning = running
        }
    }

    suspend fun setForwarded(value: Long) {
        withContext(Dispatchers.Main) {
            binding.forwarded = value.trafficTotal()
        }
    }

    suspend fun setConnections(entries: List<ConnectionEntry>) {
        withContext(Dispatchers.Main) {
            val card = binding.connectionsCard
            val container = binding.connectionsContainer

            card.visibility = if (entries.isEmpty()) View.GONE else View.VISIBLE

            while (container.childCount < entries.size) {
                ComponentConnectionBinding
                    .inflate(context.layoutInflater, container, false)
                    .apply {
                        container.addView(root)
                        root.tag = this
                    }
            }

            while (container.childCount > entries.size) {
                container.removeViewAt(container.childCount - 1)
            }

            entries.forEachIndexed { index, entry ->
                val item = container.getChildAt(index).tag as ComponentConnectionBinding
                item.address = entry.address
                item.upload = entry.uploadSpeed
                item.download = entry.downloadSpeed
                item.executePendingBindings()
            }
        }
    }

    suspend fun setMode(mode: TunnelState.Mode) {
        withContext(Dispatchers.Main) {
            binding.mode = when (mode) {
                TunnelState.Mode.Direct -> context.getString(R.string.direct_mode)
                TunnelState.Mode.Global -> context.getString(R.string.global_mode)
                TunnelState.Mode.Rule -> context.getString(R.string.rule_mode)
                else -> context.getString(R.string.rule_mode)
            }
        }
    }

    suspend fun setHasProviders(has: Boolean) {
        withContext(Dispatchers.Main) {
            binding.hasProviders = has
        }
    }

    suspend fun showAbout(versionName: String) {
        withContext(Dispatchers.Main) {
            val binding = DesignAboutBinding.inflate(context.layoutInflater).apply {
                this.versionName = versionName
            }

            AlertDialog.Builder(context)
                .setView(binding.root)
                .show()
        }
    }

    init {
        binding.self = this

        binding.colorClashStarted = context.resolveThemedColor(com.google.android.material.R.attr.colorPrimary)
        binding.colorClashStopped = context.resolveThemedColor(R.attr.colorClashStopped)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            binding.selectedTab = when (item.itemId) {
                R.id.nav_proxy -> 1
                R.id.nav_profile -> 2
                R.id.nav_settings -> 3
                else -> 0
            }
            true
        }
    }

    fun request(request: Request) {
        requests.trySend(request)
    }
}