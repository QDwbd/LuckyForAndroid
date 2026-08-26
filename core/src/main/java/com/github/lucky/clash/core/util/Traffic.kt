package com.github.lucky.clash.core.util

import com.github.lucky.clash.core.model.Traffic

fun Traffic.trafficUpload(): String {
    return trafficString(scaleTraffic(this ushr 32))
}

fun Traffic.trafficDownload(): String {
    return trafficString(scaleTraffic(this and 0xFFFFFFFF))
}

fun Traffic.trafficTotal(): String {
    val upload = scaleTraffic(this ushr 32)
    val download = scaleTraffic(this and 0xFFFFFFFF)

    return trafficString(upload + download)
}

private fun trafficString(scaled: Long): String {
    return when {
        scaled > 1024 * 1024 * 1024 * 100L -> {
            val data = scaled / 1024 / 1024 / 1024

            String.format("%.2f GiB", data.toFloat() / 100)
        }
        scaled > 1024 * 1024 * 100L -> {
            val data = scaled / 1024 / 1024

            String.format("%.2f MiB", data.toFloat() / 100)
        }
        scaled > 1024 * 100L -> {
            val data = scaled / 1024

            String.format("%.2f KiB", data.toFloat() / 100)
        }
        else -> {
            "$scaled Bytes"
        }
    }
}

private fun scaleTraffic(value: Long): Long {
    val type = (value ushr 30) and 0x3
    val data = value and 0x3FFFFFFF

    return when (type) {
        0L -> data
        1L -> data * 1024
        2L -> data * 1024 * 1024
        3L -> data * 1024 * 1024 * 1024
        else -> throw IllegalArgumentException("invalid value type")
    }
}

/**
 * 将每秒字节数格式化为便于阅读的实时网速字符串，如 "1.23 MB/s"。
 */
fun speedString(bytesPerSecond: Long): String {
    return when {
        bytesPerSecond >= 1024 * 1024 * 1024 -> {
            String.format("%.2f GB/s", bytesPerSecond / 1024.0 / 1024 / 1024)
        }
        bytesPerSecond >= 1024 * 1024 -> {
            String.format("%.2f MB/s", bytesPerSecond / 1024.0 / 1024)
        }
        bytesPerSecond >= 1024 -> {
            String.format("%.2f KB/s", bytesPerSecond / 1024.0)
        }
        else -> {
            "$bytesPerSecond B/s"
        }
    }
}