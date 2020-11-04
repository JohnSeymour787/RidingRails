package com.johnseymour.ridingrails.models.data

data class Trip(var origin: StopDetails? = null, var destination: StopDetails? = null, var favourite: Boolean = false)
{
    fun validTrip() = (origin != null) && (destination != null)
}