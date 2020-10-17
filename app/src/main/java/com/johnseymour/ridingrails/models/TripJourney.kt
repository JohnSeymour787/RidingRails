package com.johnseymour.ridingrails.models

data class TripJourney(
    val interchanges: Int,
    val legs: List<TripLeg>,
    val price: Float
)