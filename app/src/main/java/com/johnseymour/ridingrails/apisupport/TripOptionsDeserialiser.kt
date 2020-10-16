package com.johnseymour.ridingrails.apisupport

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.johnseymour.ridingrails.models.TripJourney
import com.johnseymour.ridingrails.models.TripLeg
import java.lang.reflect.Type

class TripOptionsDeserialiser: JsonDeserializer<List<TripJourney>>
{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
                            ): List<TripJourney>
    {
        val journeysJSONArray = json?.asJsonObject?.getAsJsonArray("journeys")

        val resultList = mutableListOf<TripJourney>()

        journeysJSONArray?.forEach { element ->
            element?.asJsonObject?.let {
                val interchanges = it.getAsJsonPrimitive("interchanges").asInt
                //Get the price of an adult ticket nested in "fare" object. "priceBrutto" means 'gross' in German apparently.
                val price = it.getAsJsonObject("fare")?.getAsJsonArray("tickets")
                                ?.firstOrNull()?.asJsonObject?.getAsJsonPrimitive("priceBrutto")?.asFloat
                            ?: 0F

                //Deserialise the legs array
                val legsArray = context?.deserialize<Array<TripLeg>>(
                    it.getAsJsonArray("legs"),
                    Array<TripLeg>::class.java
                                                                    )?.toList() ?: return@forEach

                resultList.add(TripJourney(interchanges, legsArray, price))
            }
        }

        return resultList
    }
}