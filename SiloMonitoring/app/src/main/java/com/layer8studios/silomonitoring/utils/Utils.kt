package com.layer8studios.silomonitoring.utils

import com.layer8studios.silomonitoring.models.Date
import com.layer8studios.silomonitoring.models.Silo
import java.time.LocalDate


object Utils {

    fun LocalDate.toDate(): Date = Date(year, monthValue, dayOfMonth)

    fun Date.toLocalDate(): LocalDate = LocalDate.of(year, month, dayOfMonth)

    fun formatText(number: Double): String = String.format("%.2f", number).replace(".", ",")

    fun getContentLeft(silo: Silo): Double {
        var contentLeft = 0.0

        silo.emptyingHistory.forEach { entry ->
            if(entry.wasAdded)
                contentLeft += entry.amount
            else contentLeft -= entry.amount
        }
        return contentLeft
    }

}