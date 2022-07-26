package com.layer8studios.silomonitoring.utils

import com.layer8studios.silomonitoring.models.Date
import com.layer8studios.silomonitoring.models.Silo
import com.layer8studios.silomonitoring.models.SiloHistoryEntry
import java.time.LocalDate


object Utils {

    fun LocalDate.toDate(): Date = Date(year, monthValue, dayOfMonth)

    fun Date.toLocalDate(): LocalDate = LocalDate.of(year, month, dayOfMonth)

    fun formatText(number: Double): String = String.format("%.2f", number).replace(".", ",")

    fun getContentLeft(silo: Silo): Double {
        var contentLeft = 0.0

        silo.emptyingHistory.forEach { entry ->
            if (entry.wasAdded)
                contentLeft += entry.amount
            else contentLeft -= entry.amount
        }
        return contentLeft
    }

    fun checkSilos() {
        val silosOriginal = Preferences.getSilos()
        val silos = Preferences.getSilos()

        var i = 0
        silos.forEach { silo ->
            val lastDate = silo.emptyingHistory.last().date.toLocalDate()
            val today = LocalDate.now()

            for(date in lastDate..today) {
                if(date == today) continue
                val dayEntries = silo.emptyingHistory.filter { it.date == date.toDate() }

                if(dayEntries.find { !it.wasAdded } == null) {
                    val entry = SiloHistoryEntry(date.toDate(), silo.needPerDay)
                    silo.emptyingHistory.add(entry)
                }
            }

            Preferences.replaceSilo(silosOriginal[i], silos[i])
            i++
        }
    }

    fun sortHistory(silo: Silo) {
        silo.emptyingHistory.sortBy { it.amount }
        silo.emptyingHistory.sortBy { it.wasAdded }
        silo.emptyingHistory.sortByDescending { it.date.toLocalDate() }
    }

}