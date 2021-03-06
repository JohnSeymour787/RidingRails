package com.johnseymour.ridingrails

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.TripOptionsFragment.Companion.TRIP_KEY
import com.johnseymour.ridingrails.models.FavouriteTripsListAdapter
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.Trip
import kotlinx.android.synthetic.main.trip_plan_fragment.*
import java.io.FileNotFoundException
import java.time.LocalDateTime

class TripPlanFragment : Fragment()
{
    companion object
    {
        const val ORIGIN_SEARCH_REQUEST = "request_stop_search_origin"
        const val DESTINATION_SEARCH_REQUEST = "request_stop_search_destination"
        const val TRIP_OPTIONS_REQUEST = "request_trip_options"
        private const val TRIP_OPTIONS_FRAGMENT_NAME = "trip_options_fragment"
    }

    private lateinit var viewModel: TripPlanViewModel
    //ActivityResultLauncher used to launch the TripOptionsActivity which can also return a result back to this activity
    private val startForResult = registerForActivityResult(object: ActivityResultContract<Unit, Trip?>() {
        //ViewModel uses its properties to create an intent with the user parameters to allow the 2nd activity to make the API calls.
        override fun createIntent(context: Context, input: Unit?): Intent = viewModel.planTripIntent(context) ?: Intent()

        override fun parseResult(resultCode: Int, intent: Intent?): Trip?
        {
            if (resultCode != RESULT_OK) {return null}

            return intent?.getParcelableExtra(TRIP_KEY)
        }
    })
    //Activity result callback
    { trip ->
        trip ?: return@registerForActivityResult
        //Add the newly-favourited Trip and notify the list's adapter
        viewModel.favouriteTrips.add(trip)
        favouriteTripsList.adapter?.notifyItemInserted(viewModel.favouriteTrips.size)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.trip_plan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripPlanViewModel::class.java)

        //Signal to the activity to add an origin fragment to its fragment container
        originName.setOnClickListener {
            parentFragmentManager.setFragmentResult(ORIGIN_SEARCH_REQUEST, bundleOf())
        }

        destinationName.setOnClickListener {
            parentFragmentManager.setFragmentResult(DESTINATION_SEARCH_REQUEST, bundleOf())
        }

        timeInput.setOnClickListener{showTimePickerDialogue()}
        dateInput.setOnClickListener{showDatePickerDialogue()}

        //Listeners for when the StopSearchFragment returns either origin or destination data
        //Update the ViewModel's Trip instance and show the name on the screen
        parentFragmentManager.apply {
            setFragmentResultListener(ORIGIN_SEARCH_KEY, viewLifecycleOwner) { _, bundle ->
                bundle.getParcelable<StopDetails>(ORIGIN_SEARCH_KEY)?.let {
                    viewModel.trip.origin = it
                    originName.text = it.disassembledName
                    showPlanButton()
                }
                parentFragmentManager.popBackStack()
            }
            setFragmentResultListener(DESTINATION_SEARCH_KEY, viewLifecycleOwner) { _, bundle ->
                bundle.getParcelable<StopDetails>(DESTINATION_SEARCH_KEY)?.let {
                    viewModel.trip.destination = it
                    destinationName.text = it.disassembledName
                    showPlanButton()
                }
                parentFragmentManager.popBackStack()
            }
            setFragmentResultListener(TRIP_OPTIONS_REQUEST, viewLifecycleOwner) { _, bundle ->
                bundle.getParcelable<Trip>(TRIP_KEY)?.let {
                    //Add the newly-favourited Trip and notify the list's adapter
                    viewModel.favouriteTrips.add(it)
                    favouriteTripsList.adapter?.notifyItemInserted(viewModel.favouriteTrips.size)
                    viewModel.trip.favourite = false
                }
            }
        }

        planTripButton.setOnClickListener {
            //Create TripOptionsFragment with necessary details and add it to the backstack
            val fragment = TripOptionsFragment.newInstance()
            fragment.arguments = viewModel.planTripBundle()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(TRIP_OPTIONS_FRAGMENT_NAME)
                .commit()
       }

        originName.text = viewModel.trip.origin?.disassembledName
        destinationName.text = viewModel.trip.destination?.disassembledName
        dateInput.text = viewModel.dateString
        timeInput.text = viewModel.timeString

        try
        {
            //Read from storage once, for this ViewModel's life
            viewModel.readFavourites(context?.openFileInput(DiskRepository.FAVOURITE_TRIPS_FILENAME)?.bufferedReader())
        }
        catch (e: FileNotFoundException) {}
        favouriteTripsList.layoutManager = LinearLayoutManager(context)
        favouriteTripsList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        favouriteTripsList.adapter = FavouriteTripsListAdapter(viewModel.favouriteTrips, ::favouriteCellClicked)

        showPlanButton()
    }

    override fun onResume()
    {
        super.onResume()

        if (viewModel.plannedTime.isBefore(LocalDateTime.now()))
        {
            viewModel.plannedTime = LocalDateTime.now()
            dateInput.text = viewModel.dateString
            timeInput.text = viewModel.timeString
        }
    }

    /**onClick listener for the dateInput TextView**/
    private fun showDatePickerDialogue()
    {
        viewModel.plannedTime.let {
            DatePickerDialog(requireContext(),
                R.style.TripPlanningDialogs,
                ::onDateSet,
                it.year,    //V DatePickerDialogue uses 0 as the starting index for months
                it.monthValue - 1,
                it.dayOfMonth).show()
        }
    }

    /**onClick listener for the timeInput TextView**/
    private fun showTimePickerDialogue()
    {
        viewModel.plannedTime.let {
            TimePickerDialog(requireContext(), R.style.TripPlanningDialogs, ::onTimeSet, it.hour, it.minute, false).show()
        }
    }

    /**Updates Date portion of ViewModel's plannedTime property and updates the date text.
     * Used as onDateSet listener for the DatePickerDialogue needed on this activity.
     * @param month : Month index of the year, January = 0*/
    private fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int)
    {
        viewModel.apply {
            plannedTime = plannedTime.updatedDate(year, month + 1, day)
        }

        dateInput.text = viewModel.dateString
    }

    /**Updates time portion of the ViewModel's plannedTime property and updates the time text.
     * Used as onTimeSet listener for the TimePickerDialogue created in this activity.*/
    private fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int)
    {
        viewModel.apply {
            plannedTime = plannedTime.updatedTime(hour, minute)
        }

        timeInput.text = viewModel.timeString
    }

    /**Called when a favourite Trip is tapped. Begins a trip plan using this origin and destination station, starting now.**/
    private fun favouriteCellClicked(trip: Trip)
    {
        //If a favourite trip is selected, then don't want to add it again if the 2nd activity returns
        //Thus, don't expect a result.
        //startActivity(viewModel.planTripIntent(requireContext(), trip))
        val fragment = TripOptionsFragment.newInstance()
        fragment.arguments = viewModel.planTripBundle(trip)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(TRIP_OPTIONS_FRAGMENT_NAME)
            .commit()
    }

    private fun showPlanButton()
    {
        if ((originName.text.isNotEmpty()) && (destinationName.text.isNotEmpty()))
        {
            planTripButton.visibility = View.VISIBLE
        }
        else
        {
            planTripButton.visibility = View.GONE
        }
    }
}

private fun LocalDateTime.updatedDate(year: Int, month: Int, day: Int): LocalDateTime = withYear(year).withMonth(month).withDayOfMonth(day)
private fun LocalDateTime.updatedTime(hour: Int, minute: Int): LocalDateTime = withHour(hour).withMinute(minute)