package com.johnseymour.ridingrails.apisupport

import com.johnseymour.ridingrails.models.StopDetails
import com.johnseymour.ridingrails.models.TripJourney
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
        "&type_origin=any" +
        "&type_destination=any" +
        "&excludedMeans=checkbox" +
        "&exclMOT_4=1" +
        "&exclMOT_5=1" +
        "&exclMOT_7=1" +
        "&exclMOT_9=1" +
        "&exclMOT_11=1" +
        "&TfNSWTR=true" +
        "&version=10.2.1.42"
        )
    fun planTrip(
        @Query("name_origin") originID: Int,
        @Query("name_destination") destinationID: Int,
        @Query("itdDate") dateString: String,
        @Query("itdTime") timeString: String,
        @Query("calcNumberOfTrips") numberOfTrips: Int = 6      //API default
        ): Call<Array<TripJourney>>
}