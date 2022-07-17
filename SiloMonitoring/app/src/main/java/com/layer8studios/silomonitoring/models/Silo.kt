package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Silo(
    val name: String,
    val capacity: Double,
    val content: String,
    val needPerDay: Double,
    val lastRefillQuantity: Double,
    val lastRefillDateYear: Int,
    val lastRefillDateMonth: Int,
    val lastRefillDateDay: Int
) : Parcelable
