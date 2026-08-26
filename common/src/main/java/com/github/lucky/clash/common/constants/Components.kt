package com.github.lucky.clash.common.constants

import android.content.ComponentName
import com.github.lucky.clash.common.util.packageName

object Components {
    private const val componentsPackageName = "com.github.lucky.clash"

    val MAIN_ACTIVITY = ComponentName(packageName, "$componentsPackageName.MainActivity")
    val PROPERTIES_ACTIVITY = ComponentName(packageName, "$componentsPackageName.PropertiesActivity")
}