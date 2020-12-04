package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.models.data.TravelMode

class ModeListAdapter(var modes: List<TravelMode>? = null): RecyclerView.Adapter<ModeListAdapter.ModeListViewHolder>()
{
    inner class ModeListViewHolder(view: TextView): RecyclerView.ViewHolder(view)
    {
        internal fun bind(mode: TravelMode)
        {
            (itemView as? TextView)?.text = mode.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeListViewHolder
    {
        /*
        val stopDetailsCell = StopSearchViewCell(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            availableModesList.layoutManager = LinearLayoutManager(parent.context)
            availableModesList.adapter = ModeListAdapter()
        }
         */
        return ModeListViewHolder(TextView(parent.context))
    }

    override fun onBindViewHolder(holder: ModeListViewHolder, position: Int)
    {
        modes?.let { holder.bind(it[position]) }
    }

    override fun getItemCount() = modes?.size ?: 0
}