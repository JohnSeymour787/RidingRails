package com.johnseymour.ridingrails.models

import androidx.lifecycle.ViewModel
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TripSearchViewModel: ViewModel()
{
    var origin = ""
    var destination = ""
    var plannedTime: ZonedDateTime = ZonedDateTime.now()
    val dateString: String
        get() = plannedTime.format(dateFormatter)
    val timeString: String
        get() = plannedTime.format(timeFormatter)

    companion object
    {
        private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).apply {
            withZone(ZoneId.systemDefault())
        }

        private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).apply {
            withZone(ZoneId.systemDefault())
        }
    }
}