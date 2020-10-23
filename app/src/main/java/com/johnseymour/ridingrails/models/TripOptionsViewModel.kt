package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository

class TripOptionsViewModel: ViewModel()
{
    lateinit var initialStopLive: LiveData<StopDetails>
    lateinit var finalDestinationLive: LiveData<StopDetails>
    lateinit var journeyOptionsLive: LiveData<List<TripJourney>>

    fun startTripPlan(origin: String, destination: String, dateString: String, timeString: String)
    {
        NetworkRepository.planTrip(origin, destination, dateString, timeString).let {
            initialStopLive = it.initialStopLive
            finalDestinationLive = it.finalDestinationLive
            journeyOptionsLive = it.journeyOptionsLive
        }
    }
}