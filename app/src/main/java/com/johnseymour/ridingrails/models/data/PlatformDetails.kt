package com.johnseymour.ridingrails.models.data

import java.time.LocalDateTime

data class PlatformDetails(
    val platformID: Int,
    val parentStopID: Int,
    val name: String,
    //Times 1 and 2 can represent either planned and estimated departure/arrival times if
    // this instance is used for origin or destination details.
    //OR, can represent planned arrival and planned departure times for instances
    // used as a stopSequence in a TripLeg. One or both can be null in this case.
    //origin -> departureEstimated
    //destination -> arrivalPlanned
    //stopSequence -> arrivalPlanned
    val time1: LocalDateTime?,
    //origin -> departurePlanned
    //destination -> arrivalEstimated
    //stopSequence -> departurePlanned
    val time2: LocalDateTime?,
)