package com.johnseymour.ridingrails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class StopSearchFragment : Fragment()
{

    companion object
    {
        fun newInstance() = StopSearchFragment()
    }

    private lateinit var viewModel: StopSearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
                             ): View?
    {
        return inflater.inflate(R.layout.stop_search_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StopSearchViewModel::class.java)
        // TODO: Use the ViewModel
    }

}