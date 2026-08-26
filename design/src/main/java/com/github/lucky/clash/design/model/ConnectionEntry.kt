package com.github.lucky.clash.design.model

/**
 * 状态页面中"连接"卡片框的一条数据。
 * [uploadSpeed] / [downloadSpeed] 为已格式化好的实时网速文本（如 "1.23 MB/s"）。
 */
data class ConnectionEntry(
    val address: String,
    val uploadSpeed: String,
    val downloadSpeed: String,
)
