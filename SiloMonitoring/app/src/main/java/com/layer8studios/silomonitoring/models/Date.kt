package com.layer8studios.silomonitoring.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Date(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int
) : Parcelable
