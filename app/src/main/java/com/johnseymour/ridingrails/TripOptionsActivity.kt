package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.models.TripJourney
import com.johnseymour.ridingrails.models.TripOptions
import com.johnseymour.ridingrails.models.TripOptionsViewModel
import com.johnseymour.ridingrails.models.TripSearchViewModel

private const val ORIGIN_KEY = "origin_key"
private const val DESTINATION_KEY = "destination_key"
private const val DATE_KEY = "date_key"
private const val TIME_KEY = "time_key"

class TripOptionsActivity : AppCompatActivity()
{
    private val viewModel by lazy {
        ViewModelProvider(this).get(TripOptionsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_options)

        //Deparcelise the API call parameters and make the ViewModel begin the call.
        //Won't need these strings beyond this point
        val originString = intent.getStringExtra(ORIGIN_KEY) ?: ""
        val destinationString = intent.getStringExtra(DESTINATION_KEY) ?: ""
        val dateString = intent.getStringExtra(DATE_KEY) ?: ""
        val timeString = intent.getStringExtra(TIME_KEY) ?: ""

        viewModel.startTripPlan(originString, destinationString, dateString, timeString)
        //Observe the API call's response, which is modularised to post an update when
        //each TripOption property is updated from an individual response.
        viewModel.tripOptionsLive.observe(this) {
            val origName = it.initialStop.disassembledName
            val destName = it.finalDestination.disassembledName
            val numberOfJourneys = it.journeyOptions.size
        }
    }

    companion object
    {
        fun planTripIntent(context: Context, origin: String, destination: String, dateString: String, timeString: String): Intent
        {
            return Intent(context, TripOptionsActivity::class.java).apply {
                putExtra(ORIGIN_KEY, origin)
                putExtra(DESTINATION_KEY, destination)
                putExtra(DATE_KEY, dateString)
                putExtra(TIME_KEY, timeString)
            }
        }
    }
}