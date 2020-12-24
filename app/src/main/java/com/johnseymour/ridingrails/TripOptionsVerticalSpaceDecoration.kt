package com.johnseymour.ridingrails

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TripOptionsVerticalSpaceDecoration(private val spacingSpacingHeight: Int): RecyclerView.ItemDecoration()
{
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State)
    {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = spacingSpacingHeight / 2
        outRect.bottom = spacingSpacingHeight / 2
    }
}