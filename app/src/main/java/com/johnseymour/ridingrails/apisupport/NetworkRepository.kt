package com.johnseymour.ridingrails.apisupport

import com.google.gson.GsonBuilder
import com.johnseymour.ridingrails.models.StopDetails
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkRepository
{
    private val tripPlannerAPI by lazy {
        val gsonBuilder = GsonBuilder().registerTypeAdapter(StopDetails::class.java, StopDetailsDeserialiser()).create()
        val gsonConverter = GsonConverterFactory.create(gsonBuilder)

        //Creating a HTTP client here to add a header to all requests
        val httpClient = OkHttpClient.Builder().addInterceptor {
           it.proceed(it.request().newBuilder().header("Authorization", "apikey U1hcbdCBk2g6Zb1ll7pHczm9Gpv7XwIRZCt9")
             .method(it.request().method(), it.request().body()).build())
        }.build()

        val retrofit = Retrofit.Builder().baseUrl("https://api.transport.nsw.gov.au/v1/tp/")
            .addConverterFactory(gsonConverter)
            .client(httpClient)
            .build()

        retrofit.create(NSWTripPlannerAPI::class.java)
    }
    fun getStopDetails(stopString: String)
    {
        val requestToMake = tripPlannerAPI.getStopDetails(stopString)

        requestToMake.enqueue(object: Callback<StopDetails>
        {
            override fun onFailure(call: Call<StopDetails>, t: Throwable)
            {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<StopDetails>, response: Response<StopDetails>)
            {
                val stopDetails = response.body()
                val cake = 2
            }
        })
    }
}