package com.github.lucky.clash.core.model

import android.os.Parcel
import android.os.Parcelable
import com.github.lucky.clash.core.util.Parcelizer
import kotlinx.serialization.Serializable

/**
 * 正在连接中的某个目标地址及其累计的上传/下载字节数。
 * 通过相邻两次采样的字节差除以时间间隔即可换算成实时网速。
 */
@Serializable
data class ConnectionTraffic(
    val address: String,
    val upload: Long,
    val download: Long,
) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Parcelizer.encodeToParcel(serializer(), parcel, this)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ConnectionTraffic> {
        override fun createFromParcel(parcel: Parcel): ConnectionTraffic {
            return Parcelizer.decodeFromParcel(serializer(), parcel)
        }

        override fun newArray(size: Int): Array<ConnectionTraffic?> {
            return arrayOfNulls(size)
        }
    }
}