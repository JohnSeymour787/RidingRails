package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.models.TripOptionListAdapter
import com.johnseymour.ridingrails.models.TripOptionsViewModel
import kotlinx.android.synthetic.main.activity_trip_options.*
import java.time.ZonedDateTime

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

        tripOptionsList.layoutManager = LinearLayoutManager(this)

        //Deparcelise the API call parameters and make the ViewModel begin the call.
        //Won't need these strings beyond this point
        val originString = intent.getStringExtra(ORIGIN_KEY) ?: ""
        val destinationString = intent.getStringExtra(DESTINATION_KEY) ?: ""
        val dateString = intent.getStringExtra(DATE_KEY) ?: ""
        val timeString = intent.getStringExtra(TIME_KEY) ?: ""

        viewModel.startTripPlan(originString, destinationString, dateString, timeString)

        //Observe the API call's response for the initial stop data
        viewModel.initialStopLive.observe(this) {
            origin.text = it.disassembledName
        }

        //Observe the API call's response for the final destination stop data
        viewModel.finalDestinationLive.observe(this) {
            destination.text = it.disassembledName
        }

        //Observe the API call's response for the full list of journey options
        viewModel.journeyOptionsLive.observe(this) {
            time.text = it.size.toString()
            tripOptionsList.adapter = TripOptionListAdapter(it)
        }
    }

    companion object
    {
        /**Return an Intent to this activity will all necessary string parameters to make a TripPlan API call.**/
        fun planTripIntent(context: Context, origin: String, destination: String, dateString: String, timeString: String, dat: ZonedDateTime? = null): Intent
        {
            return Intent(context, TripOptionsActivity::class.java).apply {
                putExtra(ORIGIN_KEY, origin)
                putExtra(DESTINATION_KEY, destination)
                putExtra(DATE_KEY, dateString)
                putExtra(TIME_KEY, timeString)
                //putExtra(TIME_KEY, dat)               //TODO() Decide to pass this or not
            }
        }
    }
}