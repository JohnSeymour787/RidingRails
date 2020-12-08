package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.StopSearchViewCell
import com.johnseymour.ridingrails.models.data.StopDetails
import kotlinx.android.synthetic.main.list_cell_stop_search.view.*

class StopSearchListAdapter(var stops: List<StopDetails>, private val onClick: ((StopDetails) -> Unit)): RecyclerView.Adapter<StopSearchListAdapter.StopSearchViewHolder>()
{
    inner class StopSearchViewHolder(view: StopSearchViewCell): RecyclerView.ViewHolder(view)
    {
        internal fun bind(stop: StopDetails)
        {
            itemView.apply {
                stopName.text = stop.disassembledName
                //Attempt to get the suburb from the latter part of the name string
                stop.name.split(", ").lastOrNull()?.let { stopSuburb.text = it }
                setOnClickListener{onClick(stop)}
                //Update the modes list adapter's data and its stop for its own onClick listener
                (availableModesList.adapter as? ModeListAdapter)?.apply {
                    modes = stop.travelModes
                    this.stop = stop
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopSearchViewHolder
    {
        val stopDetailsCell = StopSearchViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            availableModesList.layoutManager = LinearLayoutManager(parent.context)
            availableModesList.adapter = ModeListAdapter(onClick = onClick)
        }

        return StopSearchViewHolder(stopDetailsCell)
    }

    override fun onBindViewHolder(holder: StopSearchViewHolder, position: Int)
    {
        holder.bind(stops[position])
    }

    override fun getItemCount() = stops.size
}