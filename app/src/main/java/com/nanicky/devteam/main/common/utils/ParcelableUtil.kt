package com.nanicky.devteam.main.common.utils

import android.os.Parcel
import android.os.Parcelable

object ParcelableUtil {
    fun marshall(parceable: Parcelable): ByteArray {
        val parcel = Parcel.obtain()
        parceable.writeToParcel(parcel, 0)
        val bytes = parcel.marshall()
        parcel.recycle() // not sure if needed or a good idea
        return bytes
    }

    fun <T : Parcelable> unmarshall(bytes: ByteArray, creator: Parcelable.Creator<T>): T {
        val parcel = unmarshall(bytes)
        return creator.createFromParcel(parcel)
    }

    fun unmarshall(bytes: ByteArray): Parcel {
        val parcel = Parcel.obtain()
        parcel.unmarshall(bytes, 0, bytes.size)
        parcel.setDataPosition(0) // this is extremely important!
        return parcel
    }
}