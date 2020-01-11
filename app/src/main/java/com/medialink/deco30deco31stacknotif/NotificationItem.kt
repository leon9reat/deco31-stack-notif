package com.medialink.deco30deco31stacknotif

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationItem(
    var id: Int,
    var sender: String,
    var message: String
) : Parcelable {
}