package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.TripOptionListAdapter
import com.johnseymour.ridingrails.models.TripOptionsViewModel
import com.johnseymour.ridingrails.models.data.Trip
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
        tripOptionsList.addItemDecoration(TripOptionsVerticalSpaceDecoration(resources.getDimensionPixelOffset(R.dimen.list_cell_trip_option_spacing)))


        //Deparcelise the API call parameters and make the ViewModel begin the call.
        //Won't need these strings beyond this point
        //Date and time strings always exist
        val dateString = intent.getStringExtra(DATE_KEY) ?: ""
        val timeString = intent.getStringExtra(TIME_KEY) ?: ""

        //If this intent is from selecting a favourite trip, then make an API using this data
        intent.getParcelableExtra<Trip>(TRIP_KEY)?.let {
            viewModel.startTripPlan(it, dateString, timeString)
            //This trip should be a favourite, so change the icon
            if (viewModel.trip.favourite)
            {
                favouriteTrip.setImageResource(R.drawable.activity_trip_options_icon_favourite)
            }
        }
        ?:  //Otherwise, try to get the origin and destination strings to make the API call
        run {
            val originString = intent.getStringExtra(ORIGIN_KEY) ?: ""
            val destinationString = intent.getStringExtra(DESTINATION_KEY) ?: ""
            viewModel.startTripPlan(originString, destinationString, dateString, timeString)
        }

        //Observe the API call's response for the initial stop data
        viewModel.initialStopLive.observe(this) {
            when (it.status)
            {
                Status.Success ->
                {
                    //Update UI
                    origin.text = it.data?.disassembledName
                    //Update ViewModel's Trip origin field
                    viewModel.trip.origin = it?.data
                    incrementProgress()
                }

                Status.NetworkError -> showError(getString(R.string.trip_options_no_connection))
                Status.UnknownError -> showError(it.message)
            }
        }

        //Observe the API call's response for the final destination stop data
        viewModel.finalDestinationLive.observe(this) {
            when (it.status)
            {
                Status.Success ->
                {
                    //Update UI
                    destination.text = it.data?.disassembledName
                    //Update ViewModel's Trip origin field
                    viewModel.trip.destination = it?.data
                    incrementProgress()
                }

                Status.NetworkError -> showError(getString(R.string.trip_options_no_connection))
                Status.UnknownError -> showError(it.message)
            }
        }

        //Observe the API call's response for the full list of journey options
        viewModel.journeyOptionsLive.observe(this) {
            when (it.status)
            {
                Status.Success ->
                {
                    tripOptionsList.adapter = TripOptionListAdapter(it?.data ?: listOf())
                    incrementProgress()
                }

                Status.NetworkError -> showError(getString(R.string.trip_options_no_connection))
                Status.UnknownError -> showError(it.message)
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
                setResult(RESULT_OK, Intent().putExtra(TRIP_KEY, viewModel.trip))
            }
        }
    }

    /**Increments the progressBar.progress until it reaches the max, then hides it**/
    private fun incrementProgress()
    {
        progressBar.run {
            progress++

            if (progress == max)
            {
                visibility = View.GONE
            }
        }
    }

    /**Displays the error in a text field on the screen and hides all other UI elements**/
    private fun showError(errorMessage: String?)
    {
        errorDisplay.text = errorMessage
        errorDisplay.visibility = View.VISIBLE
        favouriteTrip.visibility = View.GONE
        titleText.visibility = View.GONE
        progressBar.visibility = View.GONE
        origin.visibility = View.GONE
        destination.visibility = View.GONE
    }

    companion object
    {
        const val TRIP_KEY = "trip_key"
        private const val ORIGIN_KEY = "origin_key"
        private const val DESTINATION_KEY = "destination_key"
        private const val DATE_KEY = "date_key"
        private const val TIME_KEY = "time_key"

        /**Return an Intent to this activity with all necessary string parameters to make a TripPlan API call.**/
        fun planTripIntent(context: Context, origin: String, destination: String, dateString: String, timeString: String): Intent
        {
            return Intent(context, TripOptionsActivity::class.java).apply {
                putExtra(ORIGIN_KEY, origin)
                putExtra(DESTINATION_KEY, destination)
                putExtra(DATE_KEY, dateString)
                putExtra(TIME_KEY, timeString)
            }
        }

        /**Allows for Trip planning using a favourite trip**/
        fun planTripIntent(context: Context, trip: Trip, dateString: String, timeString: String): Intent
        {
            return Intent(context, TripOptionsActivity::class.java).apply {
                putExtra(TRIP_KEY, trip)
                putExtra(DATE_KEY, dateString)
                putExtra(TIME_KEY, timeString)
            }
        }
    }
}