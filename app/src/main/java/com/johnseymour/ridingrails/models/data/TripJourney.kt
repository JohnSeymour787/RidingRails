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
    //Computed properties for data binding a TripJourney in a RecyclerView list cell
    val originPlatformName: String
        get() = legs.firstOrNull()?.origin?.name ?: ""

    val firstLine: String
        get() = legs.firstOrNull()?.lineName ?: ""

    val startTime: String                   //V Time1 for the origin station is departureEstimated
        get() = legs.firstOrNull()?.origin?.time1?.format(Constants.Formatters.timeFormatter) ?: ""

    val timeFromNow: String
        get()
        {
            legs.firstOrNull()?.origin?.time1?.let {
                //Need to create the .now() instead, even in onBind() for accurate times
                return LocalDateTime.now().until(it, ChronoUnit.MINUTES).toString()
            }
            return ""
        }
}