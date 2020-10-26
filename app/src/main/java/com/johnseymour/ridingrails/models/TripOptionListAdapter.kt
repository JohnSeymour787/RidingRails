package com.johnseymour.ridingrails.models

import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.R
import com.johnseymour.ridingrails.TripOptionViewCell
import com.johnseymour.ridingrails.models.data.TripJourney
import kotlinx.android.synthetic.main.list_cell_trip_option.view.*

class TripOptionListAdapter(private val trips: List<TripJourney>): RecyclerView.Adapter<TripOptionListAdapter.TripOptionViewHolder>()
{

    inner class TripOptionViewHolder(view: TripOptionViewCell): RecyclerView.ViewHolder(view), View.OnClickListener
    {
        internal fun bind(tripJourney: TripJourney)
        {
            itemView.apply {
                tripPrice.text = resources.getString(R.string.trip_options_cell_price, tripJourney.price)
                departTime.text = tripJourney.startTime
                timeFromNow.text = tripJourney.timeFromNow?.let { resources.getQuantityString(R.plurals.minutes_from_now, it, it) }
                platformName.text = tripJourney.originPlatformName
                firstLine.text = tripJourney.firstLine
                //Only show number of interchanges if there actually are any
                numberOfInterchanges.text = if (tripJourney.interchanges > 0) { resources.getQuantityString(R.plurals.interchanges, tripJourney.interchanges, tripJourney.interchanges) } else {""}
            }
        }

        override fun onClick(p0: View?)
        {
            //TODO("Not yet implemented")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripOptionViewHolder
    {
        val tripOptionCell = TripOptionViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        return TripOptionViewHolder(tripOptionCell)
    }

    override fun onBindViewHolder(holder: TripOptionViewHolder, position: Int)
    {
        holder.bind(trips[position])
    }

    override fun getItemCount(): Int = trips.size
}