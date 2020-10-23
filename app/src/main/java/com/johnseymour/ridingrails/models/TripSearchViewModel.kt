package com.johnseymour.ridingrails.models

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.TripOptionsActivity
import java.time.ZonedDateTime

class TripSearchViewModel: ViewModel()
{
    var origin = "central"          //TODO() Change back to empty string
    var destination = "museum"      //TODO() Change back to empty string
    var plannedTime: ZonedDateTime = ZonedDateTime.now()
    val dateString: String
        get() = plannedTime.format(Constants.Formatters.dateFormatter)
    val timeString: String
        get() = plannedTime.format(Constants.Formatters.timeFormatter)

    fun planTripIntent(context: Context): Intent
    {
        return TripOptionsActivity.planTripIntent(context, origin, destination, plannedTime.toAPIDateString(), plannedTime.toAPITimeString())
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