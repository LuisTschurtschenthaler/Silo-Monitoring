package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Silo(
    val name: String,
    val content: String,
    val capacity: Double,
    val needPerDay: Double
) : Parcelable
