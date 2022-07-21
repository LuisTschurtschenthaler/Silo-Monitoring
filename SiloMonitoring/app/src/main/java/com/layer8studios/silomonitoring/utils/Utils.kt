package com.layer8studios.silomonitoring.utils

import com.layer8studios.silomonitoring.models.Date
import com.layer8studios.silomonitoring.models.Silo
import java.time.LocalDate


object Utils {

    fun LocalDate.toDate(): Date = Date(year, monthValue, dayOfMonth)

    fun Date.toLocalDate(): LocalDate = LocalDate.of(year, month, dayOfMonth)


    fun getContentLeft(silo: Silo): Double {
        var contentLeft = silo.lastRefillQuantity

        silo.emptyingHistory.forEach { entry ->
            val nextIndex = silo.emptyingHistory.indexOf(entry) + 1

            val startDate = entry.date.toLocalDate()
            val endDate = if(nextIndex >= silo.emptyingHistory.size)
                LocalDate.now().minusDays(1)
            else silo.emptyingHistory[nextIndex].date.toLocalDate()

            for(date in startDate..endDate) {
                if(date != LocalDate.now())
                   contentLeft -= entry.needPerDay
            }
        }

        return contentLeft
    }

}