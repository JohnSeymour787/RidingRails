package com.johnseymour.ridingrails

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.list_cell_trip_option.view.*

class TripOptionViewCell(context: Context, attrs: AttributeSet? = null): ConstraintLayout(context, attrs)
{
    init
    {
        (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)?.inflate(R.layout.list_cell_trip_option, this, true)
        //Dynamically adjusts the textSize of this TextView element depending oh
        //how many lines are in its text charsequence
        timeFromNow.apply {
            doOnTextChanged { _, _, _, _ ->
                if (text.lines().size > 1)
                {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_font_size_minor))
                }
                else
                {
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_font_size_normal))
                }
            }
        }
    }
}