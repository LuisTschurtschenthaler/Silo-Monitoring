package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Silo(
    var name: String,
    var capacity: Double,
    var content: String,
    var needPerDay: Double,
    var lastRefillQuantity: Double,
    var lastRefillDate: Date,
    var contentLeft: Double
) : Parcelable
