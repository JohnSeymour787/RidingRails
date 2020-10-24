package com.johnseymour.ridingrails.apisupport

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.TravelMode
import java.lang.NullPointerException
import java.lang.reflect.Type


internal object StopDetailsDeserialiser: JsonDeserializer<StopDetails>
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): StopDetails?
    {
        val locationsArray = json?.asJsonObject?.getAsJsonArray("locations")

        //Searches the location's array in the response and returns the first appropriate location
        // (ie, a location that serves trains in this case, should mostly be the first element of this
        // array anyway)
        locationsArray?.forEach { element ->

            element?.asJsonObject?.apply {

                val id = getAsJsonPrimitive("id").asInt
                val name = getAsJsonPrimitive("name").asString ?: ""
                val disassembledName = getAsJsonPrimitive("disassembledName").asString?: ""

                //Attempt to get a valid list of modes, where TravelMode.Train is one of them
                try
                {
                    //Not all location responses have a mode array
                    val modeIntsArray = getAsJsonArray("modes").asJsonArray?.let {
                        //Get the integer list of modes
                        context?.deserialize<Array<Int>>(it, Array<Int>::class.java)?.toList()
                    }?: return@forEach      //Technically unreachable because asJsonArray will throw an error first

                    //If there are no trains available at this stop, also skip it
                    if (!trainConnection(modeIntsArray)) {return@forEach}

                    //Converting mode integer list into TravelMode list
                    val modes = mutableListOf<TravelMode>()
                    modeIntsArray.forEach { modeInt ->
                        TravelMode.valueOf(modeInt)?.let {
                            modes.add(it)
                        }
                    }
                    return StopDetails(id, name, disassembledName, modes)
                }
                catch (e: NullPointerException)
                {
                    //Continue in the loop if no modes array
                    return@forEach
                }
            }
        }
        return null
    }

    private fun trainConnection(modeInts: List<Int>): Boolean
    {
        modeInts.forEach {
            if (it == TravelMode.Train.modeID) {return true}
        }
        return false
    }
}