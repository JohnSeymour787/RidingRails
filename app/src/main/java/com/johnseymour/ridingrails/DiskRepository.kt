package com.johnseymour.ridingrails

import com.google.gson.GsonBuilder
import com.johnseymour.ridingrails.apisupport.StopDetailsDeserialiser
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.Trip
import java.io.*

object DiskRepository
{
    const val FAVOURITE_TRIPS_FILENAME = "favourite_trips.txt"

    private val gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(Array<StopDetails>::class.java, StopDetailsDeserialiser)
            .create()
    }

    fun addFavouriteTrip(trip: Trip, writer: BufferedWriter)
    {
        //Only save to disk if non-null origin and destinations. Check before writing anything
        if (!trip.validTrip())
        {
            writer.close()
            return
        }

        gson.toJson(trip.origin, writer)
        writer.write("|")       //Separator used for easy reading
        gson.toJson(trip.destination, writer)
        writer.write("|")
        writer.write(trip.favourite.toString())
        writer.newLine()
        writer.close()
    }

    fun readFavouriteTrips(reader: BufferedReader?): List<Trip>
    {
        val favourites = mutableListOf<Trip>()

        reader ?: return favourites

        //Get each Trip property from the line
        reader.forEachLine { line ->
            val lineSplit = line.split('|')
            val origin = gson.fromJson(lineSplit.firstOrNull(), StopDetails::class.java) ?: return@forEachLine
            val destination = gson.fromJson(lineSplit.getOrNull(1), StopDetails::class.java) ?: return@forEachLine
            val favourite = gson.fromJson(lineSplit.getOrNull(2), Boolean::class.java) ?: return@forEachLine

            favourites.add(Trip(origin, destination, favourite))
        }
        reader.close()

        return favourites
    }
}