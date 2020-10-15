package com.johnseymour.ridingrails.apisupport

import com.johnseymour.ridingrails.models.StopDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NSWTripPlannerAPI
{
    @GET(
        "stop_finder?outputFormat=rapidJSON" +
         "&type_sf=stop" +
         "&coordOutputFormat=EPSG%3A4326"
        )
    fun getStopDetails(@Query("name_sf") name: String): Call<StopDetails>
}