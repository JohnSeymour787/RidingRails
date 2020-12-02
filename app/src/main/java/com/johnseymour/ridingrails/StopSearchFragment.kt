package com.johnseymour.ridingrails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.StopSearchListAdapter
import com.johnseymour.ridingrails.models.StopSearchViewModel
import kotlinx.android.synthetic.main.stop_search_fragment.*

class StopSearchFragment : Fragment()
{

    companion object
    {
        fun newInstance() = StopSearchFragment()
    }

    private lateinit var viewModel: StopSearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.stop_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StopSearchViewModel::class.java)

        stopDetailsList.layoutManager = LinearLayoutManager(context)

        originInput.doOnTextChanged { text, _, _, _ ->
            if (text?.length ?:0 > 2)
            {
                viewModel.searchStops(text.toString())
                setDataObserver()
            }
        }

    }


    private fun setDataObserver()
    {
        viewModel.searchList.observe(viewLifecycleOwner) {
            when (it.status)
            {
                Status.Success ->
                {
                    it.data?.firstOrNull()?.disassembledName
                    stopDetailsList.adapter = StopSearchListAdapter(it?.data ?: listOf())
                }
            }
        }
    }
}