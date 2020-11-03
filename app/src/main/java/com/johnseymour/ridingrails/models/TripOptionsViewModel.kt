package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.apisupport.models.StatusData
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.TripJourney

class TripOptionsViewModel: ViewModel()
{
    /*As there are multiple LiveData's to observe, and any one of which can fail,
    * only want to make sure that one error message is shown to the user.
    **/
    var networkErrorOccurred = false

    lateinit var initialStopLive: LiveData<StatusData<StopDetails>>
    lateinit var finalDestinationLive: LiveData<StatusData<StopDetails>>
    lateinit var journeyOptionsLive: LiveData<StatusData<List<TripJourney>>>

    //Used to prevent multiple network calls for same data when activity has configuration change
    private var requestMade = false

    fun startTripPlan(origin: String, destination: String, dateString: String, timeString: String)
    {
        if (requestMade) {return}
        NetworkRepository.planTrip(origin, destination, dateString, timeString).let {
            initialStopLive = it.initialStopLive
            finalDestinationLive = it.finalDestinationLive
            journeyOptionsLive = it.journeyOptionsLive
        }
        requestMade = true
    }
}