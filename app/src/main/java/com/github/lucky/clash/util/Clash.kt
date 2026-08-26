package com.github.lucky.clash.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.github.lucky.clash.common.compat.startForegroundServiceCompat
import com.github.lucky.clash.common.constants.Intents
import com.github.lucky.clash.common.util.intent
import com.github.lucky.clash.design.store.UiStore
import com.github.lucky.clash.service.ClashService
import com.github.lucky.clash.service.TunService
import com.github.lucky.clash.service.util.sendBroadcastSelf

fun Context.startClashService(): Intent? {
    val startTun = UiStore(this).enableVpn

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest

        startForegroundServiceCompat(TunService::class.intent)
    } else {
        startForegroundServiceCompat(ClashService::class.intent)
    }

    return null
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP))
}