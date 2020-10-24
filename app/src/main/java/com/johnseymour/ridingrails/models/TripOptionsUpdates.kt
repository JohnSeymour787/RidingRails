package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import com.johnseymour.ridingrails.apisupport.StatusData

/**Represents all sets of LiveData needed in the TripOptions Activity. As the API call progresses
 * (with its, sub calls), each live property will be posted to with updates. Data is wrapped with
 * StatusData objects to display if they have a success or error state.
 * @param initialStopLive : Absolute beginning point of where the user currently is.
 *                          Cannot get this initialStop from JSON response for a TripJourney only,
 *                          and the legs of each journey might not start exactly where the user
 *                          currently is, for when there are multiple legs.
 * @param finalDestinationLive : Ultimate ending point for where the user wants to get to.
 * @param journeyOptionsLive : All returned journeys to get between these two points, via any number
 *                             of legs and with possibly different prices.
 **/
data class TripOptionsUpdates(
    val initialStopLive: LiveData<StatusData<StopDetails>>,
    val finalDestinationLive: LiveData<StatusData<StopDetails>>,
    val journeyOptionsLive: LiveData<StatusData<List<TripJourney>>>
)