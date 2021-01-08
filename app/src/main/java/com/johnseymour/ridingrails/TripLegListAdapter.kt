package com.johnseymour.ridingrails

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.models.TripLegViewCell
import com.johnseymour.ridingrails.models.data.TripLeg
import kotlinx.android.synthetic.main.list_cell_trip_leg.view.*

class TripLegListAdapter(private val legs: List<TripLeg>): RecyclerView.Adapter<TripLegListAdapter.TripLegViewHolder>()
{
    inner class TripLegViewHolder(view: TripLegViewCell): RecyclerView.ViewHolder(view)
    {
        internal fun bind(tripLeg: TripLeg)
        {
            itemView.apply {
                durationText.text = "Duration: " + tripLeg.duration.toString()
                lineNameText.text = tripLeg.lineName
                legOrigin?.updateUI(tripLeg.origin)
                legDestination?.updateUI(tripLeg.destination)
                //stopSequenceList.adapter =
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripLegViewHolder
    {
        val tripLegsCell = TripLegViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            stopSequenceList.layoutManager = LinearLayoutManager(parent.context)
    //        stopSequenceList.adapter = Plat
        }

        return TripLegViewHolder(tripLegsCell)
    }

    override fun onBindViewHolder(holder: TripLegViewHolder, position: Int)
    {
        holder.bind(legs[position])
    }

    override fun getItemCount() = legs.size
}