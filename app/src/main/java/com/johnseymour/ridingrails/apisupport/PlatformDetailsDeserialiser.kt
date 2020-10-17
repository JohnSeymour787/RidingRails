package com.johnseymour.ridingrails.apisupport

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.johnseymour.ridingrails.models.PlatformDetails
import java.lang.reflect.Type
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class PlatformDetailsDeserialiser: JsonDeserializer<PlatformDetails>
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): PlatformDetails?
    {
        val platformDetailsJSON = json?.asJsonObject ?: return null
        val parentJSON = platformDetailsJSON.getAsJsonObject("parent") ?: return null

        val platformID = platformDetailsJSON.getAsJsonPrimitive("id").asInt
        val parentStopID = parentJSON.getAsJsonPrimitive("id").asInt
        val name = parentJSON.getAsJsonPrimitive("disassembledName").asString ?: ""

        //Strings can differ for what this Platform detail represents, or may be null (see PlatformDetails definition)
        val time1String: String? = platformDetailsJSON.run {
            getAsJsonPrimitive("arrivalTimePlanned")?.asString
            ?: getAsJsonPrimitive("departureTimeEstimated")?.asString
        }

        val time2String: String? = platformDetailsJSON.run {
            getAsJsonPrimitive("arrivalTimeEstimated")?.asString
            ?: getAsJsonPrimitive("departureTimePlanned")?.asString
        }

        val time1 = timeStringToObject(time1String)
        val time2 = timeStringToObject(time2String)

        return PlatformDetails(platformID, parentStopID, name, time1, time2)
    }

    companion object
    {
        private val formatter = DateTimeFormatter.ISO_DATE_TIME.apply {
            withZone(ZoneId.systemDefault())
        }
        //TODO() This could be made an extension to the string class, might need it later for getting user input
        private fun timeStringToObject(time: String?): ZonedDateTime?
        {
            return try
            {
                ZonedDateTime.parse(time, formatter) ?: null
            }
            catch (e: NullPointerException)
            {
                null
            }
        }
    }
}