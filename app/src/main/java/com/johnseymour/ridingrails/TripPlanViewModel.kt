package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.Trip
import java.io.BufferedReader
import java.time.LocalDateTime

class TripPlanViewModel : ViewModel()
{
    val trip = Trip()
    var plannedTime: LocalDateTime = LocalDateTime.now()
    val dateString: String
        get() = plannedTime.format(Constants.Formatters.dateFormatter)
    val timeString: String
        get() = plannedTime.format(Constants.Formatters.timeFormatter)

    var favouriteTrips = mutableListOf<Trip>()

    private var storageRead = false

    fun planTripIntent(context: Context, trip: Trip = this.trip): Intent
    {
        return TripOptionsActivity.planTripIntent(context, trip, plannedTime.format(
            Constants.Formatters.APIDateFormatter), plannedTime.toAPITimeString())
    }

    fun readFavourites(reader: BufferedReader?)
    {
        if (storageRead) {return}
        favouriteTrips = DiskRepository.readFavouriteTrips(reader).toMutableList()
        storageRead = true
    }

    /**Extension method to get time string in a format that the API accepts**/
    private fun LocalDateTime.toAPITimeString(): String
    {
        //API always requires leading 0s in this string
        return "$hour".padStart(2, '0') +
               "$minute".padStart(2, '0')
    }
}