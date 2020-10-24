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

private const val ORIGIN_KEY = "origin_key"
private const val DESTINATION_KEY = "destination_key"
private const val DATE_KEY = "date_key"
private const val TIME_KEY = "time_key"

class TripOptionsActivity : AppCompatActivity()
{
    private val viewModel by lazy {
        ViewModelProvider(this).get(TripOptionsViewModel::class.java)
    }

    /*As there are multiple LiveData's to observe, and any one of which can fail,
    * only want to make sure that one error message is shown to the user.
    **/
    private var networkErrorOccurred = false

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
                origin.text = it.data?.disassembledName
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
    }

    private fun showNetworkError(errorMessage: String?)
    {
        if (!networkErrorOccurred)
        {
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            networkErrorOccurred = true
        }
    }

    companion object
    {
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