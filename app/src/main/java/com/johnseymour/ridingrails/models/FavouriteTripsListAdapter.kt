package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.models.data.Trip
import kotlinx.android.synthetic.main.list_cell_favourite_trips.view.*

class FavouriteTripsListAdapter(var favouriteList: List<Trip>): RecyclerView.Adapter<FavouriteTripsListAdapter.FavouriteTripsViewHolder>()
{
    inner class FavouriteTripsViewHolder(view: FavouriteTripViewCell): RecyclerView.ViewHolder(view)
    {
        internal fun bind(trip: Trip)
        {
            itemView.apply {
                originStationName.text = trip.origin?.disassembledName
                destinationStationName.text = trip.destination?.disassembledName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteTripsViewHolder
    {
        val favouriteTripCell = FavouriteTripViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        return FavouriteTripsViewHolder(favouriteTripCell)
    }

    override fun onBindViewHolder(holder: FavouriteTripsViewHolder, position: Int)
    {
        holder.bind(favouriteList[position])
    }

    override fun getItemCount(): Int = favouriteList.size
}