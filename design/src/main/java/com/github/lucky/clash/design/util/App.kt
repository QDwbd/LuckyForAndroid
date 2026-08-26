package com.github.lucky.clash.design.util

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.github.lucky.clash.common.compat.foreground
import com.github.lucky.clash.design.model.AppInfo

fun PackageInfo.toAppInfo(pm: PackageManager): AppInfo {
    return AppInfo(
        packageName = packageName,
        icon = applicationInfo!!.loadIcon(pm).foreground(),
        label = applicationInfo!!.loadLabel(pm).toString(),
        installTime = firstInstallTime,
        updateDate = lastUpdateTime,
    )
}
