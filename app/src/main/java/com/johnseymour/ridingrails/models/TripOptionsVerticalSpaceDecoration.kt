package com.johnseymour.ridingrails.models

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TripOptionsVerticalSpaceDecoration(private val spacingSpacingHeight: Int): RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = spacingSpacingHeight
    }
}