package com.johnseymour.ridingrails.models.data

import android.content.res.Resources
import com.johnseymour.ridingrails.Constants
import com.johnseymour.ridingrails.R.plurals
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

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

    /**Calculates and returns a plural-string representing the time from now, depending on how
     * long the event is away.**/
    fun timeFromNow(resources: Resources): String?
    {
        legs.firstOrNull()?.origin?.time1?.let {
            //Need to create the .now() instead, even in onBind() for accurate times
            val totalMinutes = LocalDateTime.now().until(it, ChronoUnit.MINUTES).toInt()

            return when(totalMinutes.absoluteValue)
            {
                //Minute value only
                in 0 until MINUTES_PER_HOUR -> resources.getQuantityString(plurals.minutes_from_now, totalMinutes, totalMinutes)

                //1 hour up to 24 hours
                in MINUTES_PER_HOUR until MINUTES_PER_DAY ->
                {
                    val totalHours = totalMinutes / MINUTES_PER_HOUR
                    //Minutes need to be absolute because don't want the second measurement to be negative
                    val remainingMinutes = totalMinutes.absoluteValue % MINUTES_PER_HOUR

                    resources.getQuantityString(plurals.hours_from_now, totalHours, totalHours) +
                    "\r\n" +
                    resources.getQuantityString(plurals.minutes_from_now, remainingMinutes, remainingMinutes)
                }

                //1 day to 10 days
                in MINUTES_PER_DAY until MINUTES_MAX_DAYS_WITH_HOUR_SHOWN ->
                {
                    val totalHours = totalMinutes / MINUTES_PER_HOUR
                    val totalDays = totalHours / HOURS_PER_DAY
                    val remainingHours = totalHours.absoluteValue % HOURS_PER_DAY

                    resources.getQuantityString(plurals.days_from_now, totalDays, totalDays) +
                    "\r\n" +
                    resources.getQuantityString(plurals.hours_from_now, remainingHours, remainingHours)
                }

                //For 10 or more days, only show number of days
                else -> resources.getQuantityString(plurals.days_from_now, totalMinutes / MINUTES_PER_DAY, totalMinutes / MINUTES_PER_DAY)
            }
        }
        return null
    }

    companion object
    {
        private const val MINUTES_PER_HOUR = 60
        private const val HOURS_PER_DAY = 24
        private const val MINUTES_PER_DAY = 1440
        private const val MAX_DAYS_WITH_HOUR_SHOWN = 10
        private const val MINUTES_MAX_DAYS_WITH_HOUR_SHOWN = MAX_DAYS_WITH_HOUR_SHOWN * MINUTES_PER_DAY
    }
}