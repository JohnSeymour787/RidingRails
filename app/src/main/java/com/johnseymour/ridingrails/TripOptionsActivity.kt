package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.TripOptionListAdapter
import com.johnseymour.ridingrails.models.TripOptionsViewModel
import kotlinx.android.synthetic.main.activity_trip_options.*

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
            if (it.status == Status.SUCCESS)
            {
                //Update UI
                origin.text = it.data?.disassembledName
                //Update ViewModel's Trip origin field
                viewModel.trip.origin = it?.data
            }
            else
            {
                showNetworkError(it.message)
            }
        }

        //Observe the API call's response for the final destination stop data
        viewModel.finalDestinationLive.observe(this) {
            if (it.status == Status.SUCCESS)
            {
                destination.text = it?.data?.disassembledName
                viewModel.trip.destination = it?.data
            }
            else
            {
                showNetworkError(it.message)
            }
        }

        //Observe the API call's response for the full list of journey options
        viewModel.journeyOptionsLive.observe(this) {
            if (it.status == Status.SUCCESS)
            {
                time.text = it?.data?.size.toString()
                tripOptionsList.adapter = TripOptionListAdapter(it?.data ?: listOf())
            }
            else
            {
                showNetworkError(it.message)
            }
        }

        //Write trip details to disk and update favourite icon
        favouriteTrip.setOnClickListener {
            //Don't want to write same trip to disk twice
            if (!viewModel.trip.favourite)
            {
                viewModel.favouriteTrip(openFileOutput(DiskRepository.FAVOURITE_TRIPS_FILENAME, MODE_APPEND).bufferedWriter())
                favouriteTrip.setImageResource(R.drawable.activity_trip_options_icon_favourite)
                //Return the new favourited trip to add to the list of favourites without reading the file again
                setResult(RESULT_OK, Intent().putExtra(FAVOURITE_TRIP_KEY, viewModel.trip))
            }
        }
    }

    private fun showNetworkError(errorMessage: String?)
    {
        if (!viewModel.networkErrorOccurred)
        {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.networkErrorOccurred = true
        }
    }

    companion object
    {
        const val FAVOURITE_TRIP_KEY = "favourite_trip_key"
        private const val ORIGIN_KEY = "origin_key"
        private const val DESTINATION_KEY = "destination_key"
        private const val DATE_KEY = "date_key"
        private const val TIME_KEY = "time_key"

        /**Return an Intent to this activity will all necessary string parameters to make a TripPlan API call.**/
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