package com.johnseymour.ridingrails.models

//TODO () COORDS and list of modes
data class StopDetails(val id: Int, val name: String, val disassembledName: String, val modes: List<TravelMode>)
{

}