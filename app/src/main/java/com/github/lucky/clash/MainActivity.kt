package com.github.lucky.clash

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.os.SystemClock
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.lucky.clash.common.util.intent
import com.github.lucky.clash.common.util.ticker
import com.github.lucky.clash.design.MainDesign
import com.github.lucky.clash.design.model.ConnectionEntry
import com.github.lucky.clash.design.ui.ToastDuration
import com.github.lucky.clash.util.startClashService
import com.github.lucky.clash.util.stopClashService
import com.github.lucky.clash.util.withClash
import com.github.lucky.clash.util.withProfile
import com.github.lucky.clash.core.bridge.*
import com.github.lucky.clash.core.util.speedString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import com.github.lucky.clash.design.R

class MainActivity : BaseActivity<MainDesign>() {
    override suspend fun main() {
        val design = MainDesign(this)

        setContentDesign(design)

        design.fetch()

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ActivityStart,
                        Event.ServiceRecreated,
                        Event.ProfileLoaded, Event.ProfileChanged -> design.fetch()
                        Event.ClashStop -> {
                            design.fetch()
                            design.setConnections(emptyList())
                            resetConnectionSample()
                        }
                        Event.ClashStart -> {
                            design.fetch()
                            resetConnectionSample()
                        }
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        MainDesign.Request.ToggleStatus -> {
                            if (clashRunning)
                                stopClashService()
                            else
                                design.startClash()
                        }
                        MainDesign.Request.OpenProxy ->
                            startActivity(ProxyActivity::class.intent)
                        MainDesign.Request.OpenProfiles ->
                            startActivity(ProfilesActivity::class.intent)
                        MainDesign.Request.OpenProviders ->
                            startActivity(ProvidersActivity::class.intent)
                        MainDesign.Request.OpenLogs -> {
                            if (LogcatService.running) {
                                startActivity(LogcatActivity::class.intent)
                            } else {
                                startActivity(LogsActivity::class.intent)
                            }
                        }
                        MainDesign.Request.OpenSettings ->
                            startActivity(SettingsActivity::class.intent)
                        MainDesign.Request.OpenHelp ->
                            startActivity(HelpActivity::class.intent)
                        MainDesign.Request.OpenAbout ->
                            design.showAbout(queryAppVersionName())
                    }
                }
                if (clashRunning) {
                    ticker.onReceive {
                        design.fetchTraffic()
                        design.fetchConnections()
                    }
                }
            }
        }
    }

    private suspend fun MainDesign.fetch() {
        setClashRunning(clashRunning)

        val state = withClash {
            queryTunnelState()
        }
        val providers = withClash {
            queryProviders()
        }

        setMode(state.mode)
        setHasProviders(providers.isNotEmpty())

        withProfile {
            setProfileName(queryActive()?.name)
        }
    }

    private suspend fun MainDesign.fetchTraffic() {
        withClash {
            setForwarded(queryTrafficTotal())
        }
    }

    // 上一次采样的每个地址累计上传/下载字节数（用于计算实时网速）
    private var lastConnectionSample = mutableMapOf<String, Pair<Long, Long>>()
    private var lastSampleTime = 0L

    private fun resetConnectionSample() {
        lastConnectionSample.clear()
        lastSampleTime = 0L
    }

    private suspend fun MainDesign.fetchConnections() {
        val connections = withClash {
            queryConnections()
        }

        val now = SystemClock.elapsedRealtime()
        val elapsed = if (lastSampleTime == 0L) 0L else now - lastSampleTime

        val entries = connections.mapNotNull { c ->
            val prev = lastConnectionSample[c.address]
            val uploadSpeed = if (prev != null && elapsed > 0)
                ((c.upload - prev.first) * 1000L / elapsed).coerceAtLeast(0L) else 0L
            val downloadSpeed = if (prev != null && elapsed > 0)
                ((c.download - prev.second) * 1000L / elapsed).coerceAtLeast(0L) else 0L

            // 上传和下载实时速度都为 0 的地址不显示
            if (uploadSpeed == 0L && downloadSpeed == 0L) {
                null
            } else {
                ConnectionEntry(c.address, speedString(uploadSpeed), speedString(downloadSpeed))
            }
        }

        lastConnectionSample.clear()
        lastConnectionSample.putAll(connections.associate { it.address to (it.upload to it.download) })
        lastSampleTime = now

        setConnections(entries)
    }

    private suspend fun MainDesign.startClash() {
        val active = withProfile { queryActive() }

        if (active == null || !active.imported) {
            showToast(R.string.no_profile_selected, ToastDuration.Long) {
                setAction(R.string.profiles) {
                    startActivity(ProfilesActivity::class.intent)
                }
            }

            return
        }

        val vpnRequest = startClashService()

        try {
            if (vpnRequest != null) {
                val result = startActivityForResult(
                    ActivityResultContracts.StartActivityForResult(),
                    vpnRequest
                )

                if (result.resultCode == RESULT_OK)
                    startClashService()
            }
        } catch (e: Exception) {
            design?.showToast(R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun queryAppVersionName(): String {
        return withContext(Dispatchers.IO) {
            packageManager.getPackageInfo(packageName, 0).versionName + "\n" + Bridge.nativeCoreVersion().replace("_", "-")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(RequestPermission()
                ) { isGranted: Boolean ->
                }
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

val mainActivityAlias = "${MainActivity::class.java.name}Alias"