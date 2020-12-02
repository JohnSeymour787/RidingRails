package com.johnseymour.ridingrails

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout

class StopSearchViewCell(context: Context, attrs: AttributeSet? = null): ConstraintLayout(context, attrs)
{
    init {(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)?.inflate(R.layout.list_cell_stop_search, this, true)}
}