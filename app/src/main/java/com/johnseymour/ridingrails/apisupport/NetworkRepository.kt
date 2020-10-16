package com.johnseymour.ridingrails.apisupport

import com.google.gson.GsonBuilder
import com.johnseymour.ridingrails.models.StopDetails
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class NetworkRepository
{
    private val tripPlannerAPI by lazy {
        val gsonBuilder = GsonBuilder().registerTypeAdapter(StopDetails::class.java, StopDetailsDeserialiser()).create()
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

    fun planTrip(origin: String, destination: String)
    {
        combine(getStopDetails(origin), getStopDetails(destination)).success {
            val origin = it.first
            val destination = it.second

            val cake = 2
        }
    }

    fun getStopDetails(stopString: String): Promise<StopDetails, Throwable>
    {
        val deferred = deferred<StopDetails, Throwable>()

        val requestToMake = tripPlannerAPI.getStopDetails(stopString.toLowerCase(Locale.getDefault()))

        requestToMake.enqueue(object: Callback<StopDetails>
        {
            override fun onResponse(call: Call<StopDetails>, response: Response<StopDetails>)
            {
                val stopDetails = response.body()?.let {
                    deferred.resolve(it)
                }
                val cake = 2
            }

            override fun onFailure(call: Call<StopDetails>, t: Throwable)
            {
                TODO("Not yet implemented")
            }
        })

        return deferred.promise
    }
}