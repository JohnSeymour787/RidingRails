package com.johnseymour.ridingrails

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.apisupport.TripLegDeserialiser
import com.johnseymour.ridingrails.models.TripSearchViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.time.ZonedDateTime

class TripSearchActivity : AppCompatActivity()
{
    private val viewModel by lazy {
        ViewModelProvider(this).get(TripSearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkController = NetworkRepository()
        networkController.planTrip("sydney central", destinationInput?.text?.toString() ?: "", "20201016", "1200")

        originInput.doOnTextChanged { text, start, before, count ->
            viewModel.origin = text.toString()
        }

        destinationInput.doOnTextChanged { text, start, before, count ->
            viewModel.destination = text.toString()
        }

        timeInput.text = viewModel.timeString
        dateInput.text = viewModel.dateString
    }


    /**onClick listener for the dateInput TextView**/
    fun showDatePickerDialogue(v: View)
    {
        viewModel.plannedTime.let {                                                                         //V DatePickerDialogue uses 0 as the starting index for months
            DatePickerDialog(this, R.style.TripPlanningDialogs, ::onDateSet, it.year, it.monthValue - 1, it.dayOfMonth).show()
        }
    }
    /**onClick listener for the timeInput TextView**/
    fun showTimePickerDialogue(v: View)
    {
        viewModel.plannedTime.let {
            TimePickerDialog(this, R.style.TripPlanningDialogs, ::onTimeSet, it.hour, it.minute, false).show()
        }
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
}

//TODO() V Probably have a method in the ViewModel to make the intent, packaging this into it
/**Returns a string from the DatePicker's date in a format that the API accepts**/
private fun DatePicker.toAPIDateString(): String = "$year$month$dayOfMonth"

private fun ZonedDateTime.updatedDate(year: Int, month: Int, day: Int): ZonedDateTime = withYear(year).withMonth(month).withDayOfMonth(day)
private fun ZonedDateTime.updatedTime(hour: Int, minute: Int): ZonedDateTime = withHour(hour).withMinute(minute)