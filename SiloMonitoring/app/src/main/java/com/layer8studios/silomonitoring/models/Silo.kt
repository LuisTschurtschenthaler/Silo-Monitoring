package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class SiloHistoryEntry(
    val date: Date,
    val amount: Double,
    val wasAdded: Boolean = false
) : Parcelable


@Parcelize
data class Silo(
    var name: String,
    var capacity: Double,
    var content: String,
    var needPerDay: Double,
    var daysBeforeNotification: Long,
    var emptyingHistory: MutableList<SiloHistoryEntry> = mutableListOf(),
    val notificationID: Int = Calendar.getInstance().timeInMillis.toInt()
) : Parcelable
