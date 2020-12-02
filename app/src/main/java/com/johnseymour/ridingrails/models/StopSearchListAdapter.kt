package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.StopSearchViewCell
import com.johnseymour.ridingrails.models.data.StopDetails
import kotlinx.android.synthetic.main.list_cell_stop_search.view.*

class StopSearchListAdapter(private val stops: List<StopDetails>): RecyclerView.Adapter<StopSearchListAdapter.StopSearchViewHolder>()
{
    inner class StopSearchViewHolder(view: StopSearchViewCell): RecyclerView.ViewHolder(view)
    {
        internal fun bind(stop: StopDetails)
        {
            itemView.apply {
                stopName.text = stop.disassembledName
                //Probably some other data to show here, such as the modes (or an icon that changes
                //based on mode type)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopSearchViewHolder
    {
        val stopDetailsCell = StopSearchViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        return StopSearchViewHolder(stopDetailsCell)
    }

    override fun onBindViewHolder(holder: StopSearchViewHolder, position: Int)
    {
        holder.bind(stops[position])
    }

    override fun getItemCount() = stops.size
}