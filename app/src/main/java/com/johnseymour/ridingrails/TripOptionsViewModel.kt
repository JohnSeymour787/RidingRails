package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.apisupport.models.StatusData
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.Trip
import com.johnseymour.ridingrails.models.data.TripJourney
import java.io.BufferedWriter

class TripOptionsViewModel : ViewModel()
{
    lateinit var initialStopLive: LiveData<StatusData<StopDetails>>
    lateinit var finalDestinationLive: LiveData<StatusData<StopDetails>>
    lateinit var journeyOptionsLive: LiveData<StatusData<List<TripJourney>>>

    var trip: Trip = Trip()

    //Used to prevent multiple network calls for same data when activity has configuration change
    private var requestMade = false

    fun startTripPlan(trip: Trip, dateString: String, timeString: String)
    {
        if (requestMade) {return}

        this.trip = trip

        NetworkRepository.planTrip(trip, dateString, timeString).let {
            initialStopLive = it.initialStopLive
            finalDestinationLive = it.finalDestinationLive
            journeyOptionsLive = it.journeyOptionsLive
        }
        requestMade = true
    }

    fun favouriteTrip(writer: BufferedWriter?)
    {
        writer ?: return
        trip.favourite = true       //Unfavouriting not currently supported
        DiskRepository.addFavouriteTrip(trip, writer)
    }
}