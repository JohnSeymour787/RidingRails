package com.johnseymour.ridingrails.apisupport

import com.johnseymour.ridingrails.models.StopDetails
import com.johnseymour.ridingrails.models.TripLeg
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NSWTripPlannerAPI
{
    @GET("stop_finder?outputFormat=rapidJSON" +
         "&type_sf=stop" +
         "&coordOutputFormat=EPSG%3A4326" +
         "&TfNSWSF=true" +
         "&version=10.2.1.42"
        )
    fun getStopDetails(@Query("name_sf") name: String): Call<StopDetails>

    @GET(
        "trip?outputFormat=rapidJSON" +
        "&coordOutputFormat=EPSG%3A4326" +
        "&depArrMacro=dep" +
        "&itdDate=20201016" +
        "&itdTime=1200" +
        "&type_origin=any" +
        "&name_origin=10101100" +
        "&type_destination=any" +
        "&name_destination=10101115" +
        "&calcNumberOfTrips=6" +
        "&excludedMeans=checkbox" +
        "&exclMOT_4=1" +
        "&exclMOT_5=1" +
        "&exclMOT_7=1" +
        "&exclMOT_9=1" +
        "&exclMOT_11=1" +
        "&TfNSWTR=true" +
        "&version=10.2.1.42"
        )
    fun planTrip(): Call<TripLeg>
}