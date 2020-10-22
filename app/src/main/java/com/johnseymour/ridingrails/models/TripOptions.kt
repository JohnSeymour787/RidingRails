package com.johnseymour.ridingrails.models

/**Represents all data needed to pass to the TripOptions Activity.
 * @param initialStop : Absolute beginning point of where the user currently is.
 *                      Cannot get this initialStop from JSON response for a TripJourney only,
 *                      and the legs of each journey might not start exactly where the user
 *                      currently is, for when there are multiple legs.
 * @param finalDestination : Ultimate ending point for where the user wants to get to.
 * @param journeyOptions : All returned journeys to get between these two points, via any number
 *                         of legs and with possibly different prices.
 **/
data class TripOptions(
    val initialStop: StopDetails,
    val finalDestination: StopDetails,
    val journeyOptions: List<TripJourney>
)