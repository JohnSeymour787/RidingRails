package com.johnseymour.ridingrails.models

//TODO () COORDS <- EDIT, maybe ignore coordinates everywhere for now
data class StopDetails(val id: Int, val name: String, val disassembledName: String, val modes: List<TravelMode>)