package com.johnseymour.ridingrails.apisupport

import com.google.gson.*
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.TravelMode
import java.lang.NullPointerException
import java.lang.reflect.Type


internal object StopDetailsDeserialiser: JsonDeserializer<StopDetails>
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): StopDetails?
    {
        val topLevelJSON = json?.asJsonObject ?: return null

        topLevelJSON.getAsJsonArray("locations")?.forEach { element ->
            //Searches the location's array in the response and returns the first appropriate location
            // (ie, a location that serves trains in this case, should mostly be the first element of this
            // array anyway)
            element?.asJsonObject?.let {
                return deserialiseSingleStop(it, context) ?: return@forEach
            }
        }
        //Otherwise, assume this JSON is for a single StopDetails object, so try to deserialise it like one
        return deserialiseSingleStop(topLevelJSON, context)
    }

    private fun deserialiseSingleStop(stopTopLevel: JsonObject, context: JsonDeserializationContext?): StopDetails?
    {
        with (stopTopLevel)
        {
            val id = getAsJsonPrimitive("id").asInt
            val name = getAsJsonPrimitive("name").asString ?: ""
            val disassembledName = getAsJsonPrimitive("disassembledName").asString ?: ""

            //Try to convert the travel modes directly from the enum values (if deserialising from the text file)
            getAsJsonArray("travelModes")?.let {travelModesArray ->
                context?.deserialize<Array<TravelMode>>(travelModesArray, Array<TravelMode>::class.java)?.toList()?.let {
                    return StopDetails(id, name, disassembledName, it)
                }
            }
            //Otherwise. attempt to get a valid list of modes from an array of ints returned from the API, where TravelMode.Train is one of them
            val modes = convertToModes(getAsJsonArray("modes"), context) ?: return null

            return StopDetails(id, name, disassembledName, modes)
        }
    }

    private fun convertToModes(modeIntsJSON: JsonArray?, context: JsonDeserializationContext?): List<TravelMode>?
    {
        //Get the integer list of modes
        val modeIntsArray = context?.deserialize<Array<Int>>(modeIntsJSON, Array<Int>::class.java)?.toList() ?: return null

        //If there are no trains available at this stop, also skip it
        if (!trainConnection(modeIntsArray)) { return null }

        //Converting mode integer list into TravelMode list
        val modes = mutableListOf<TravelMode>()
        modeIntsArray.forEach { modeInt ->
            TravelMode.valueOf(modeInt)?.let {
                modes.add(it)
            }
        }

        return modes
    }

    private fun trainConnection(modeInts: List<Int>): Boolean
    {
        modeInts.forEach {
            if (it == TravelMode.Train.modeID) {return true}
        }
        return false
    }
}