package com.johnseymour.ridingrails.models

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.Constants
import com.johnseymour.ridingrails.DiskRepository
import com.johnseymour.ridingrails.TripOptionsActivity
import com.johnseymour.ridingrails.models.data.Trip
import java.io.BufferedReader
import java.time.LocalDateTime

class TripSearchViewModel: ViewModel()
{
    var origin = "central"          //TODO() Change back to empty string
    var destination = "museum"      //TODO() Change back to empty string
    var plannedTime: LocalDateTime = LocalDateTime.now()
    val dateString: String
        get() = plannedTime.format(Constants.Formatters.dateFormatter)
    val timeString: String
        get() = plannedTime.format(Constants.Formatters.timeFormatter)

    var favouriteTrips = mutableListOf<Trip>()

    private var storageRead = false

    fun planTripIntent(context: Context): Intent
    {
        return TripOptionsActivity.planTripIntent(context, origin, destination, plannedTime.format(
            Constants.Formatters.APIDateFormatter), plannedTime.toAPITimeString())
    }

    fun planFavouriteTripIntent(context: Context, trip: Trip): Intent
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