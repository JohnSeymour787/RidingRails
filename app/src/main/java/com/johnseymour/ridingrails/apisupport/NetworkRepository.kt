package com.johnseymour.ridingrails.apisupport

import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.johnseymour.ridingrails.models.*
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale


object NetworkRepository
{
    private val stopDetailsCache by lazy {
        mutableMapOf<String, StopDetails>()
    }

    private val tripPlannerAPI by lazy {
        val gsonBuilder = GsonBuilder().run {
            registerTypeAdapter(StopDetails::class.java, StopDetailsDeserialiser)
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
                    header("Authorization", "apikey U1hcbdCBk2g6Zb1ll7pHczm9Gpv7XwIRZCt9")
                    method(it.request().method(), it.request().body())
                }.build()

                it.proceed(request)
            }.build()
        }.build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.transport.nsw.gov.au/v1/tp/")
        .addConverterFactory(gsonConverter)
        .client(httpClient)
        .build()

        retrofit.create(NSWTripPlannerAPI::class.java)
    }


    fun planTrip(originString: String, destinationString: String, dateString: String, timeString: String): TripOptionsUpdates
    {
        //Calling activities can individually observe each separate API call as it gets a response
        val originLiveData = MutableLiveData<StopDetails>()
        val destinationLiveData = MutableLiveData<StopDetails>()
        val plannedTripsLiveData = MutableLiveData<List<TripJourney>>()
        //Wait until both promises for retrieving stop details are resolved before making TripPlan API call
        combine(getStopDetails(originString, originLiveData), getStopDetails(destinationString, destinationLiveData)).success {
            val origin = it.first
            val destination = it.second

            val requestCall = tripPlannerAPI.planTrip(origin.id, destination.id, dateString, timeString)

            //Make asynchronous call to TripPlanner endpoint
            requestCall.enqueue(object: Callback<Array<TripJourney>>
            {
                override fun onResponse(call: Call<Array<TripJourney>>, response: Response<Array<TripJourney>>)
                {
                    val trip = response.body()?.toList() ?: return
                    plannedTripsLiveData.postValue(trip)
                }

                override fun onFailure(call: Call<Array<TripJourney>>, t: Throwable)
                {
                    t.localizedMessage
                }
            })
        }.fail {
            val error = it.localizedMessage
            val cak = 2
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

    private fun getStopDetails(stopString: String, liveData: MutableLiveData<StopDetails>): Promise<StopDetails, Throwable>
    {
        val deferred = deferred<StopDetails, Throwable>()
        val searchTerm = stopString.toLowerCase(Locale.getDefault())

        //If the cache has the stop in it, use this and immediately return
        searchStopDetailCache(searchTerm)?.let {
            deferred.resolve(it)
            return deferred.promise
        }

        //Otherwise, need to make new API call
        val requestToMake = tripPlannerAPI.getStopDetails(searchTerm)

        requestToMake.enqueue(object: Callback<StopDetails>
        {
            override fun onResponse(call: Call<StopDetails>, response: Response<StopDetails>)
            {
                response.body()?.let {
                    //Adding this response to the cache, indexed by the disassembled name (doesn't include ", Sydney" at end)
                    stopDetailsCache[it.disassembledName] = it
                    //Add reference to same StopDetails but using what the user entered as the index, because they might type
                    //something similar next time
                    stopDetailsCache[searchTerm] = it

                    liveData.postValue(it)

                    //Resolve the promise for this deferred
                    deferred.resolve(it)
                }
            }

            //Failure error needs to be handled elsewhere
            override fun onFailure(call: Call<StopDetails>, t: Throwable) = deferred.reject(t)
        })

        return deferred.promise
    }
}