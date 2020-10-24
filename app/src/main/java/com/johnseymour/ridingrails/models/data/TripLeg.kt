package com.johnseymour.ridingrails.models.data

data class TripLeg(
    val tripCode: Int,
    val duration: Int,
    val lineName: String,
    val origin: PlatformDetails,
    val destination: PlatformDetails,
    val stopSequence: List<PlatformDetails>
)