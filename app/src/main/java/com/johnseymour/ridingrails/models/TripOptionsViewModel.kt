package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository

class TripOptionsViewModel: ViewModel()
{
    lateinit var tripOptionsLive: LiveData<TripOptions>

    fun startTripPlan(origin: String, destination: String, dateString: String, timeString: String)
    {
        tripOptionsLive = NetworkRepository().planTrip(origin, destination, dateString, timeString)
    }
}