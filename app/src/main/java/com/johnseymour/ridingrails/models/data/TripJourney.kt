package com.johnseymour.ridingrails.models.data

import com.johnseymour.ridingrails.Constants
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class TripJourney(
    val interchanges: Int,
    val legs: List<TripLeg>,
    val price: Float
)
{
    /**Computed properties for data binding a TripJourney in a RecyclerView list cell**/

    val originPlatformName: String
        get() = legs.firstOrNull()?.origin?.name?.split(", ")?.lastOrNull() ?: ""

    val firstLine: String
        get() = legs.firstOrNull()?.lineName ?: ""

    val firstLineColor: Int?
        get() = legs.firstOrNull()?.lineColor

    val startTime: String                   //V Time1 for the origin station is departureEstimated
        get() = legs.firstOrNull()?.origin?.time1?.format(Constants.Formatters.timeFormatter) ?: ""

    //Returns null because don't want any number to be returned if cannot find this data.
    //Any number, including 0 or negatives, is a valid value.
    val timeFromNow: Int?
        get()
        {
            legs.firstOrNull()?.origin?.time1?.let {
                //Need to create the .now() instead, even in onBind() for accurate times
                return LocalDateTime.now().until(it, ChronoUnit.MINUTES).toInt()
            }
            return null
        }
}