package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.R
import com.johnseymour.ridingrails.models.data.Trip
import kotlinx.android.synthetic.main.list_cell_favourite_trips.view.*

class FavouriteTripsListAdapter(private val favouriteList: List<Trip>, private val cellClick: (Trip) -> Unit): RecyclerView.Adapter<FavouriteTripsListAdapter.FavouriteTripsViewHolder>()
{
    inner class FavouriteTripsViewHolder(view: FavouriteTripViewCell): RecyclerView.ViewHolder(view)
    {
        internal fun bind(trip: Trip)
        {
            itemView.apply {
                favouriteTripDescription.text = resources.getString(R.string.activity_main_favourite_list_text, trip.origin?.disassembledName, trip.destination?.disassembledName)
                setOnClickListener{cellClick(trip)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteTripsViewHolder
    {
        val favouriteTripCell = FavouriteTripViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0,0,0,resources.getDimensionPixelSize(R.dimen.list_cell_trip_option_spacing))
            }
        }

        return FavouriteTripsViewHolder(favouriteTripCell)
    }

    override fun onBindViewHolder(holder: FavouriteTripsViewHolder, position: Int)
    {
        holder.bind(favouriteList[position])
    }

    override fun getItemCount(): Int = favouriteList.size
}