package com.johnseymour.ridingrails.models

data class StopDetails(
    val id: Int,
    val name: String,
    val disassembledName: String,
    val modes: List<TravelMode>
)