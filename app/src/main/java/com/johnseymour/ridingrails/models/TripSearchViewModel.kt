package com.johnseymour.ridingrails.models

import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository
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

    fun planTrip()
    {
        NetworkRepository().planTrip(origin, destination, plannedTime.toAPIDateString(), plannedTime.toAPITimeString())
    }

    companion object
    {
        private val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).apply {
            withZone(ZoneId.systemDefault())
        }

        private val timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).apply {
            withZone(ZoneId.systemDefault())
        }
    }

    /**Extension methods to get date and time strings in a format that the API accepts**/
    private fun ZonedDateTime.toAPIDateString(): String
    {
        //API always requires leading 0s in this string
        return "$year".padStart(4, '0') +
               "$monthValue".padStart(2, '0') +
               "$dayOfMonth".padStart(2, '0')
    }
    private fun ZonedDateTime.toAPITimeString(): String
    {
        //API always requires leading 0s in this string
        return "$hour".padStart(2, '0') +
               "$minute".padStart(2, '0')
    }
}