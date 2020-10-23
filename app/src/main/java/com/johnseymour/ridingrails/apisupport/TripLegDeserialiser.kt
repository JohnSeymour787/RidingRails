package com.johnseymour.ridingrails.apisupport

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.johnseymour.ridingrails.models.PlatformDetails
import com.johnseymour.ridingrails.models.TravelMode
import com.johnseymour.ridingrails.models.TripLeg
import java.lang.reflect.Type

internal object TripLegDeserialiser: JsonDeserializer<TripLeg>
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TripLeg?
    {
        val legJSON = json?.asJsonObject ?: return null

        legJSON.getAsJsonObject("transportation")?.let {
            //Get the modeID first and make sure that this is a Train leg
            val modeID = it.getAsJsonObject("product")?.getAsJsonPrimitive("class")?.asInt ?: 0
            if (TravelMode.valueOf(modeID) != TravelMode.Train) {return null}

            val duration = legJSON.getAsJsonPrimitive("duration")?.asInt ?: 0
            val name = it.getAsJsonPrimitive("disassembledName")?.asString ?: ""
            val tripCode = it.getAsJsonObject("properties")?.getAsJsonPrimitive("tripCode")?.asInt ?: 0

            val origin = context?.deserialize<PlatformDetails>(legJSON.getAsJsonObject("origin"), PlatformDetails::class.java)
                         ?: return null
            val destination = context.deserialize<PlatformDetails>(legJSON.getAsJsonObject("destination"), PlatformDetails::class.java)
                              ?: return null
            val stopSequence = context.deserialize<Array<PlatformDetails>>(legJSON.getAsJsonArray("stopSequence"), Array<PlatformDetails>::class.java)
                               ?.toList()
                               ?: return null

            return TripLeg(tripCode, duration, name, origin, destination, stopSequence)
        }
        return null
    }
}