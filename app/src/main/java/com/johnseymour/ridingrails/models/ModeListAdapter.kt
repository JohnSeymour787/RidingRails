package com.johnseymour.ridingrails.models

import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.johnseymour.ridingrails.models.data.StopDetails
import com.johnseymour.ridingrails.models.data.TravelMode

class ModeListAdapter(var modes: List<TravelMode>? = null, var stop: StopDetails? = null, private val onClick: ((StopDetails) -> Unit)): RecyclerView.Adapter<ModeListAdapter.ModeListViewHolder>()
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

        val textView = TextView(parent.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            //Clicking any item in the ModeList always has the same response as
            //clicking the StopSearch item that this adapter is a part of.
            setOnClickListener { stop?.let { onClick(it) } }
        }

        return ModeListViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ModeListViewHolder, position: Int)
    {
        modes?.let { holder.bind(it[position]) }
    }

    override fun getItemCount() = modes?.size ?: 0
}