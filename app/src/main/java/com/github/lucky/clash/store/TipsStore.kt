package com.github.lucky.clash.store

import android.content.Context
import com.github.lucky.clash.common.store.Store
import com.github.lucky.clash.common.store.asStoreProvider

class TipsStore(context: Context) {
    private val store = Store(
        context
            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .asStoreProvider()
    )

    var requestDonate: Boolean by store.boolean(
        key = "request_donate",
        defaultValue = true,
    )

    var primaryVersion: Int by store.int(
        key = "primary_version",
        defaultValue = -1,
    )

    companion object {
        const val CURRENT_PRIMARY_VERSION = 1

        private const val FILE_NAME = "tips"
    }
}