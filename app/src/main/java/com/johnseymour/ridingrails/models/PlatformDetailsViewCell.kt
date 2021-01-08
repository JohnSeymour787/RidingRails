package com.johnseymour.ridingrails.models

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.johnseymour.ridingrails.Constants
import com.johnseymour.ridingrails.R
import com.johnseymour.ridingrails.models.data.PlatformDetails
import kotlinx.android.synthetic.main.list_cell_platform_details.view.*

class PlatformDetailsViewCell(context: Context, attrs: AttributeSet? = null): ConstraintLayout(context, attrs)
{
    init
    {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)?.inflate(R.layout.list_cell_platform_details, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.PlatformDetailsViewCell, 0, 0).apply {
            try
            {
                //Setting the time1 and time2 label texts depending on this custom attribute
                // see comments in the PlatformDetails class for explanations
                when (getInteger(R.styleable.PlatformDetailsViewCell_PlatformDetailsType, 2))
                {
                    //Origin times
                    0 ->
                    {
                        time1Label.text = resources.getText(R.string.list_cell_platform_details_time_label_est_dep)
                        time2Label.text = resources.getText(R.string.list_cell_platform_details_time_label_pln_dep)
                    }
                    //Destination times
                    1 ->
                    {
                        time1Label.text = resources.getText(R.string.list_cell_platform_details_time_label_pln_arr)
                        time2Label.text = resources.getText(R.string.list_cell_platform_details_time_label_est_arr)
                    }
                    //StopSequence times
                    else ->
                    {
                        time1Label.text = resources.getText(R.string.list_cell_platform_details_time_label_pln_arr)
                        time2Label.text = resources.getText(R.string.list_cell_platform_details_time_label_pln_dep)
                    }
                }
            }
            finally {recycle()}
        }
    }

    fun updateUI(platformDetails: PlatformDetails)
    {
        platformDetails.apply {
            platformNameText.text = name
        }

        //If time1 exists, update the text string and make it visible
        platformDetails.time1?.let {
            time1Text.text = Constants.Formatters.timeFormatter.format(it)
            time1Text.visibility = View.VISIBLE
            time1Label.visibility = View.VISIBLE
        }//Otherwise, make the text and its label be GONE
        ?: run {
            time1Text.visibility = View.GONE
            time1Label.visibility = View.GONE
        }

        platformDetails.time2?.let {
            time2Text.text = Constants.Formatters.timeFormatter.format(it)
            time2Text.visibility = View.VISIBLE
            time2Label.visibility = View.VISIBLE
        }?: run {
            time2Text.visibility = View.GONE
            time2Label.visibility = View.GONE
        }
    }
}