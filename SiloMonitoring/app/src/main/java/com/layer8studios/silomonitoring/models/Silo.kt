package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SiloHistoryEntry(
    val date: Date,
    val amount: Double,
    val wasAdded: Boolean = false
) : Parcelable


// TODO(ADD DELETE LIST)
@Parcelize
data class Silo(
    var name: String,
    var capacity: Double,
    var content: String,
    var needPerDay: Double,
    var daysBeforeNotification: Long,
    var emptyingHistory: MutableList<SiloHistoryEntry> = mutableListOf(),
    val notificationID: Int = (0..Int.MAX_VALUE).random()
) : Parcelable
