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
            if(entry.wasAdded)
                contentLeft += entry.amount
            else contentLeft -= entry.amount
        }
        return contentLeft
    }

    fun checkSilo(silo: Silo): Silo {
        val newSilo = silo.copy()

        val lastDate = silo.emptyingHistory.last().date.toLocalDate()
        val today = LocalDate.now()

        for(date in lastDate..today) {
            if(date == today) continue
            val dayEntries = newSilo.emptyingHistory.filter { it.date == date.toDate() }

            if(dayEntries.find { !it.wasAdded } == null) {
                val entry = SiloHistoryEntry(date.toDate(), newSilo.needPerDay)
                newSilo.emptyingHistory.add(entry)
            }
        }

        return newSilo
    }

    fun checkSilos() {
        val silos = Preferences.getSilos()
        silos.forEach { silo ->
            val newSilo = checkSilo(silo)
            Preferences.replaceSilo(silo, newSilo)
        }
    }

    fun sortHistory(silo: Silo) {
        silo.emptyingHistory.sortBy { it.amount }
        silo.emptyingHistory.sortBy { it.wasAdded }
        silo.emptyingHistory.sortByDescending { it.date.toLocalDate() }
    }

}