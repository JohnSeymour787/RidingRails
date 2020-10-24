package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.apisupport.StatusData

class TripOptionsViewModel: ViewModel()
{
    lateinit var initialStopLive: LiveData<StatusData<StopDetails>>
    lateinit var finalDestinationLive: LiveData<StatusData<StopDetails>>
    lateinit var journeyOptionsLive: LiveData<StatusData<List<TripJourney>>>

    fun startTripPlan(origin: String, destination: String, dateString: String, timeString: String)
    {
        NetworkRepository.planTrip(origin, destination, dateString, timeString).let {
            initialStopLive = it.initialStopLive
            finalDestinationLive = it.finalDestinationLive
            journeyOptionsLive = it.journeyOptionsLive
        }
    }
}