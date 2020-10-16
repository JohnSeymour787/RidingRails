package com.johnseymour.ridingrails.models

import java.time.LocalTime

data class PlatformDetails(
    val platformID: Int,
    val parentStopID: Int,
    val name: String,
    //Can represent either planned and estimated departure/arrival if this
    //instance is used for origin or destination details.
    //OR, can represent planned arrival and planned departure times for instances
    //used as a stop sequence in a TripLeg. Sometimes both can be null in this case.
    val time1: LocalTime?,
    val time2: LocalTime?,
)