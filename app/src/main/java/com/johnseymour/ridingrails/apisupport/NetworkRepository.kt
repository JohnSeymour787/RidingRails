package com.johnseymour.ridingrails.apisupport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.johnseymour.ridingrails.apisupport.models.StatusData
import com.johnseymour.ridingrails.models.data.*
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.Locale


object NetworkRepository
{
    private const val API_DEV_KEY = "apikey U1hcbdCBk2g6Zb1ll7pHczm9Gpv7XwIRZCt9"
    private const val API_BASE_URL = "https://api.transport.nsw.gov.au/v1/tp/"


    private val stopDetailsCache by lazy {
        mutableMapOf<String, StopDetails>()
    }

    private val tripPlannerAPI by lazy {
        val gsonBuilder = GsonBuilder().run {
            registerTypeAdapter(Array<StopDetails>::class.java, StopDetailsDeserialiser)
            registerTypeAdapter(PlatformDetails::class.java, PlatformDetailsDeserialiser)
            registerTypeAdapter(TripLeg::class.java, TripLegDeserialiser)
            registerTypeAdapter(Array<TripJourney>::class.java, TripJourneyArrayDeserialiser)
            create()
        }
        val gsonConverter = GsonConverterFactory.create(gsonBuilder)

        //Creating a HTTP client here to add a header to all requests
        val httpClient = OkHttpClient.Builder().apply {
            addInterceptor {
                val request = it.request().newBuilder().apply {
                    header("Authorization", API_DEV_KEY)
                    method(it.request().method(), it.request().body())
                }.build()

                it.proceed(request)
            }.build()
        }.build()

        val retrofit = Retrofit.Builder().baseUrl(API_BASE_URL)
            .addConverterFactory(gsonConverter)
            .client(httpClient)
            .build()

        retrofit.create(NSWTripPlannerAPI::class.java)
    }

    /**Makes an asynchronous call to the TripPlanner endpoint**/
    private fun getTripPlan(originID: Int, destinationID: Int, dateString: String, timeString: String, liveData: MutableLiveData<StatusData<List<TripJourney>>>)
    {
        val requestCall = tripPlannerAPI.planTrip(originID, destinationID, dateString, timeString)

        requestCall.enqueue(object: Callback<Array<TripJourney>>
        {
            override fun onResponse(call: Call<Array<TripJourney>>, response: Response<Array<TripJourney>>)
            {
                response.body()?.toList()?.let { body ->
                    //Post a successful status-wrapped data
                    liveData.postValue(StatusData.success(body))
                    //TODO() add more specific Status enums to allow for error message localisation, see
                    // onResponse() method below. This error generally shouldn't occur anyway.
                } ?: liveData.postValue(StatusData.generalFailure("Trip Planner response body not found"))
            }

            override fun onFailure(call: Call<Array<TripJourney>>, t: Throwable)
            {
                if (t is IOException)
                {
                    liveData.postValue(StatusData.networkError())
                }
                else
                {
                    //Post a failed status-wrapped data
                    liveData.postValue(StatusData.generalFailure(t.localizedMessage))
                }
            }
        })
    }

    fun planTrip(originString: String, destinationString: String, dateString: String, timeString: String): TripOptionsUpdates
    {
        //Calling activities can individually observe each separate API call as it gets a response
        val originLiveData = MutableLiveData<StatusData<StopDetails>>()
        val destinationLiveData = MutableLiveData<StatusData<StopDetails>>()
        val plannedTripsLiveData = MutableLiveData<StatusData<List<TripJourney>>>()
        //Wait until both promises for retrieving stop details are resolved before making the TripPlan API call
        combine(getStopDetails(originString, originLiveData), getStopDetails(destinationString, destinationLiveData)).success {
            val origin = it.first
            val destination = it.second
            getTripPlan(originID = origin.id, destinationID = destination.id, dateString = dateString, timeString = timeString, liveData = plannedTripsLiveData)
        }

        return TripOptionsUpdates(originLiveData, destinationLiveData, plannedTripsLiveData)
    }

    fun planTrip(trip: Trip, dateString: String, timeString: String): TripOptionsUpdates
    {
        val originLiveData = MutableLiveData<StatusData<StopDetails>>()
        val destinationLiveData = MutableLiveData<StatusData<StopDetails>>()
        val plannedTripsLiveData = MutableLiveData<StatusData<List<TripJourney>>>()

        //Already have origin and destination IDs, so no need to make a new getStopDetails API request
        trip.origin?.let {
            originLiveData.postValue(StatusData.success(it))
        }
        ?:  //If no origin station, then need to post errors here and to the plannedTrips LiveData, at it cannot now work
        run {
            originLiveData.postValue(StatusData.generalFailure("Could not find origin station details!"))
            plannedTripsLiveData.postValue(StatusData.generalFailure("Could not find origin station details!"))
        }

        trip.destination?.let {
            destinationLiveData.postValue(StatusData.success(it))
        }
        ?:  //If no destination station, then need to post errors here and to the plannedTrips LiveData, at it cannot work
        run {
            destinationLiveData.postValue(StatusData.generalFailure("Could not find destination station details!"))
            plannedTripsLiveData.postValue(StatusData.generalFailure("Could not find destination station details!"))
        }

        //Will only make the trip plan API call if the origin and destination details are non-null
        trip.run {
            origin?.id?.let { originID ->
                destination?.id?.let { destID ->
                    getTripPlan(originID, destID, dateString, timeString, plannedTripsLiveData)
                }
            }
        }

        return TripOptionsUpdates(originLiveData, destinationLiveData, plannedTripsLiveData)
    }


    /** Searches the stopDetailsCache Map<String, StopDetails> for a search term.
     * @return StopDetails if found, or null if nothing.**/
    private fun searchStopDetailCache(searchTerm: String): StopDetails?
    {
        //"Searching" the cache by filtering keys and taking first value
        return stopDetailsCache.filterKeys { it.toLowerCase(Locale.getDefault()).contains(searchTerm) }.values.firstOrNull()
    }


    private fun getStopDetails(stopString: String, liveData: MutableLiveData<StatusData<StopDetails>>): Promise<StopDetails, Throwable>
    {
        val deferred = deferred<StopDetails, Throwable>()
        val searchTerm = stopString.toLowerCase(Locale.getDefault())

        //If the cache has the stop in it, use this and immediately return
        searchStopDetailCache(searchTerm)?.let {
            liveData.postValue(StatusData.success(it))
            deferred.resolve(it)
            return deferred.promise
        }

        //Otherwise, need to make new API call
        val requestToMake = tripPlannerAPI.getStopDetails(searchTerm)

        requestToMake.enqueue(object: Callback<Array<StopDetails>>
        {
            override fun onResponse(call: Call<Array<StopDetails>>, response: Response<Array<StopDetails>>)
            {
                response.body()?.let {
                    //Adding this response to the cache, indexed by the disassembled name (doesn't include ", Sydney" at end)
              //      stopDetailsCache[it.disassembledName] = it.firstOrNull()
                    //Add reference to same StopDetails but using what the user entered as the index, because they might type
                    //something similar next time
            //        stopDetailsCache[searchTerm] = it

                //    liveData.postValue(StatusData.success(it))

                    //Resolve the promise for this deferred
             //       deferred.resolve(it)
                    //TODO() Add "Stop not found error" Status enum and allow for the observing activity to get a localised string resource
                } ?: liveData.postValue(StatusData.generalFailure("Couldn't find \"$stopString\" station. Please try another search"))
            }

            //Failure error needs to be handled elsewhere
            override fun onFailure(call: Call<Array<StopDetails>>, t: Throwable)
            {
                //If a network error occurred (part of IOException)
                if (t is IOException)
                {
                    //Post a network error. Error text will be sent elsewhere
                    liveData.postValue(StatusData.networkError())
                }
                //Otherwise, post the localised general failure
                else
                {
                    liveData.postValue(StatusData.generalFailure(t.localizedMessage))
                }
                deferred.reject(t)
            }
        })

        return deferred.promise
    }

    fun getSingleStopDetails(stopString: String): LiveData<StatusData<List<StopDetails>>>
    {
        val liveData = MutableLiveData<StatusData<List<StopDetails>>>()
        val searchTerm = stopString.toLowerCase(Locale.getDefault())

        /*
        TODO() Maybe don't need the cache at all
        //If the cache has the stop in it, use this and immediately return
        searchStopDetailCache(searchTerm)?.let {
            liveData.postValue(StatusData.success(it))
            deferred.resolve(it)
            return deferred.promise
        }
        */

        //Otherwise, need to make new API call
        val requestToMake = tripPlannerAPI.getStopDetails(searchTerm)

        requestToMake.enqueue(object: Callback<Array<StopDetails>>
        {
            override fun onResponse(call: Call<Array<StopDetails>>, response: Response<Array<StopDetails>>)
            {
                response.body()?.let {
                    //Sort the list alphabetically by disassembled name
                    liveData.postValue(StatusData.success(it.toList().sortedWith(compareBy { element -> element.disassembledName })))

                    //TODO() Add "Stop not found error" Status enum and allow for the observing activity to get a localised string resource
                } ?: liveData.postValue(StatusData.generalFailure("Couldn't find \"$stopString\" station. Please try another search"))
            }

            //Failure error needs to be handled elsewhere
            override fun onFailure(call: Call<Array<StopDetails>>, t: Throwable)
            {
                //If a network error occurred (part of IOException)
                if (t is IOException)
                {
                    //Post a network error. Error text will be sent elsewhere
                    liveData.postValue(StatusData.networkError())
                }
                //Otherwise, post the localised general failure
                else
                {
                    liveData.postValue(StatusData.generalFailure(t.localizedMessage))
                }
            }
        })

        return liveData
    }
}