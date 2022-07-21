package com.layer8studios.silomonitoring.utils

import java.time.LocalDate


operator fun LocalDate.rangeTo(other: LocalDate) =
    DateProgression(this, other)


class DateIterator(
    private val start: LocalDate,
    private val endInclusive: LocalDate,
    private val daySteps: Long = 1
): Iterator<LocalDate> {

    private var currentDate = start

    override fun hasNext() = (currentDate <= endInclusive)

    override fun next(): LocalDate {
        val next = currentDate
        currentDate = currentDate.plusDays(daySteps)
        return next
    }
}

class DateProgression(
    override val start: LocalDate,
    override val endInclusive: LocalDate,
    private val daySteps: Long = 1
) : Iterable<LocalDate>, ClosedRange<LocalDate> {

    override fun iterator(): Iterator<LocalDate> =
        DateIterator(start, endInclusive, daySteps)

    infix fun step(days: Long) = DateProgression(start, endInclusive, days)
}
