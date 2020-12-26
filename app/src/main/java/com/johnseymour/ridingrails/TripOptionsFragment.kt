package com.johnseymour.ridingrails

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.TripOptionListAdapter
import com.johnseymour.ridingrails.models.data.Trip
import com.johnseymour.ridingrails.models.data.TripJourney
import kotlinx.android.synthetic.main.trip_options_fragment.*

class TripOptionsFragment : Fragment()
{
    private lateinit var viewModel: TripOptionsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.trip_options_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripOptionsViewModel::class.java)

        tripOptionsList.layoutManager = LinearLayoutManager(context)
        tripOptionsList.addItemDecoration(TripOptionsVerticalSpaceDecoration(resources.getDimensionPixelOffset(R.dimen.list_cell_trip_option_spacing)))

        //Deparcelise the API call parameters and make the ViewModel begin the call.
        //Won't need these strings beyond this point
        //Date and time strings always exist
        val dateString = arguments?.getString(DATE_KEY) ?: ""
        val timeString = arguments?.getString(TIME_KEY) ?: ""

        //If this intent is from selecting a favourite trip, then make an API using this data
        arguments?.getParcelable<Trip>(TRIP_KEY)?.let {
            viewModel.startTripPlan(it, dateString, timeString)
            //This trip should be a favourite, so change the icon
            if (viewModel.trip.favourite)
            {
                favouriteTrip.setImageResource(R.drawable.activity_trip_options_icon_favourite)
            }
        } ?: return

        //Observe the API call's response for the initial stop data
        viewModel.initialStopLive.observe(viewLifecycleOwner) {
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

                Status.NetworkError -> showError(getString(R.string.no_internet_connection))
                Status.UnknownError -> showError(it.message)
            }
        }

        //Observe the API call's response for the final destination stop data
        viewModel.finalDestinationLive.observe(viewLifecycleOwner) {
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

                Status.NetworkError -> showError(getString(R.string.no_internet_connection))
                Status.UnknownError -> showError(it.message)
            }
        }

        //Observe the API call's response for the full list of journey options
        viewModel.journeyOptionsLive.observe(viewLifecycleOwner) {
            when (it.status)
            {
                Status.Success ->
                {
                    tripOptionsList.adapter = TripOptionListAdapter(it?.data ?: listOf(), ::showTripDetails)
                    incrementProgress()
                }

                Status.NetworkError -> showError(getString(R.string.no_internet_connection))
                Status.UnknownError -> showError(it.message)
            }
        }

        /*
        //Write trip details to disk and update favourite icon
        favouriteTrip.setOnClickListener {
            //Don't want to write same trip to disk twice
            if (!viewModel.trip.favourite)
            {
                viewModel.favouriteTrip(openFileOutput(DiskRepository.FAVOURITE_TRIPS_FILENAME,
                    AppCompatActivity.MODE_APPEND
                                                      ).bufferedWriter())
                favouriteTrip.setImageResource(R.drawable.activity_trip_options_icon_favourite)
                //Return the new favourited trip to add to the list of favourites without reading the file again
                setResult(AppCompatActivity.RESULT_OK, Intent().putExtra(TripOptionsActivity.TRIP_KEY, viewModel.trip))
            }
        }

         */
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

    private fun showTripDetails(tripJourney: TripJourney)
    {
        detailContainerView.visibility = View.VISIBLE

        val fragment = TripJourneyDetailFragment.newInstance(tripJourney)
        parentFragmentManager.beginTransaction()
            .replace(R.id.detailContainerView, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object
    {
        const val TRIP_KEY = "trip_key"
        const val DATE_KEY = "date_key"
        const val TIME_KEY = "time_key"

        fun newInstance() = TripOptionsFragment()
    }
}