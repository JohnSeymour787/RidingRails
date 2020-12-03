package com.johnseymour.ridingrails

import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.models.FavouriteTripsListAdapter
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
    }

    private lateinit var viewModel: TripPlanViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.trip_plan_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripPlanViewModel::class.java)

        parentFragmentManager.apply {
            setFragmentResultListener(ORIGIN_SEARCH_KEY, viewLifecycleOwner) { _, bundle ->
                viewModel.trip.origin = bundle.getParcelable(ORIGIN_SEARCH_KEY)
                originName.text = viewModel.trip.origin?.disassembledName
                parentFragmentManager.popBackStack()
            }
            setFragmentResultListener(DESTINATION_SEARCH_KEY, viewLifecycleOwner) { _, bundle ->
                viewModel.trip.destination = bundle.getParcelable(DESTINATION_SEARCH_KEY)
                destinationName.text = viewModel.trip.destination?.disassembledName
                parentFragmentManager.popBackStack()
            }
        }

        //Signal to the activity to add an origin fragment to its fragment container
        originName.setOnClickListener {
            parentFragmentManager.setFragmentResult(ORIGIN_SEARCH_REQUEST, bundleOf())
        }

        destinationName.setOnClickListener {
            parentFragmentManager.setFragmentResult(DESTINATION_SEARCH_REQUEST, bundleOf())
        }

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

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {return}

        if (requestCode == TRIP_OPTIONS_REQUEST)
        {
            //Add the newly-favourited Trip and notify the list's adapter
            viewModel.favouriteTrips.add(data?.getParcelableExtra<Trip>(TripOptionsActivity.TRIP_KEY) ?: return)
            favouriteTripsList.adapter?.notifyItemInserted(viewModel.favouriteTrips.size)
        }
    }

    /**onClick listener for the dateInput TextView**/
    fun showDatePickerDialogue(v: View)
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
    fun showTimePickerDialogue(v: View)
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
     * Used as onTimeSet listent for the TimePickerDialogue created in this activity.*/
    private fun onTimeSet(timePicker: TimePicker, hour: Int, minute: Int)
    {
        viewModel.apply {
            plannedTime = plannedTime.updatedTime(hour, minute)
        }

        timeInput.text = viewModel.timeString
    }

    fun planNewTrip(v: View)
    {
       // if (validateFields())
    //    {
            //ViewModel uses its properties to create an intent with the user parameters
            //to allow the 2nd activity to make the API calls.
            startActivityForResult(viewModel.planTripIntent(requireContext()), TRIP_OPTIONS_REQUEST)
   //     }
    }

    /**Called when a favourite Trip is tapped. Begins a trip plan using this origin and destination station, starting now.**/
    private fun favouriteCellClicked(trip: Trip)
    {
        //If a favourite trip is selected, then don't want to add it again if the 2nd activity returns
        //Thus, don't expect a result.
        startActivity(viewModel.planTripIntent(requireContext(), trip))
    }
}

private const val TRIP_OPTIONS_REQUEST = 0

private fun LocalDateTime.updatedDate(year: Int, month: Int, day: Int): LocalDateTime = withYear(year).withMonth(month).withDayOfMonth(day)
private fun LocalDateTime.updatedTime(hour: Int, minute: Int): LocalDateTime = withHour(hour).withMinute(minute)