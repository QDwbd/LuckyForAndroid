package com.github.lucky.clash.service.util

import com.github.lucky.clash.service.data.ImportedDao
import com.github.lucky.clash.service.data.PendingDao
import java.util.*

suspend fun generateProfileUUID(): UUID {
    var result = UUID.randomUUID()

    while (ImportedDao().exists(result) || PendingDao().exists(result)) {
        result = UUID.randomUUID()
    }

    return result
}
