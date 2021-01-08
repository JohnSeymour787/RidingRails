package com.johnseymour.ridingrails.models

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.johnseymour.ridingrails.R
import kotlinx.android.synthetic.main.list_cell_platform_details.view.*

class PlatformDetailsViewCell(context: Context, attrs: AttributeSet? = null): ConstraintLayout(context, attrs)
{
    init
    {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)?.inflate(R.layout.list_cell_platform_details, this, true)

        //Setting the time1 and time2 label texts depending on this custom attribute
        // see comments in the PlatformDetails class for explanations
        when (attrs?.getAttributeIntValue(R.styleable.PlatformDetailsVieCell_PlatformDetailsType, 0))
        {
            //Origin times
            0 ->
            {
                time1Label.text = resources.getText(R.string.list_cell_platform_details_time_label_est_dep)
                time2Label.text = resources.getText(R.string.list_cell_platform_details_time_label_pln_dep)
            }
            //Destination times
            2 ->
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
}