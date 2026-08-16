package com.github.lucky.clash.core.model

import android.os.Parcel
import android.os.Parcelable
import com.github.lucky.clash.common.util.createListFromParcelSlice
import com.github.lucky.clash.common.util.writeToParcelSlice

class ConnectionTrafficList(data: List<ConnectionTraffic>) : List<ConnectionTraffic> by data, Parcelable {
    constructor(parcel: Parcel) : this(ConnectionTraffic.createListFromParcelSlice(parcel, 0, 50))

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        return writeToParcelSlice(parcel, flags)
    }

    companion object CREATOR : Parcelable.Creator<ConnectionTrafficList> {
        override fun createFromParcel(parcel: Parcel): ConnectionTrafficList {
            return ConnectionTrafficList(parcel)
        }

        override fun newArray(size: Int): Array<ConnectionTrafficList?> {
            return arrayOfNulls(size)
        }
    }
}
