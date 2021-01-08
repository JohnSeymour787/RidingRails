package com.johnseymour.ridingrails.models

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.johnseymour.ridingrails.R

class PlatformDetailsViewCell(context: Context, attrs: AttributeSet? = null): ConstraintLayout(context, attrs)
{
    init {(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater)?.inflate(R.layout.list_cell_platform_details, this, true)}
}