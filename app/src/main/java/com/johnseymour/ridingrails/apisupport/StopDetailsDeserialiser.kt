package com.johnseymour.ridingrails.apisupport

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.johnseymour.ridingrails.models.StopDetails
import java.lang.reflect.Type


//TODO make internal and singleton
class StopDetailsDeserialiser: JsonDeserializer<StopDetails>
{
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
        ): StopDetails
    {


        return StopDetails(1,"2", "name 2")
    }
}