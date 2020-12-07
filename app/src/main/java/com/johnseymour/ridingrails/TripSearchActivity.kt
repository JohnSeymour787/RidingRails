package com.johnseymour.ridingrails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.johnseymour.ridingrails.models.TripSearchViewModel

const val ORIGIN_SEARCH_KEY = "origin"
const val DESTINATION_SEARCH_KEY = "destination"

class TripSearchActivity : AppCompatActivity()
{
    private val viewModel by lazy {
        ViewModelProvider(this).get(TripSearchViewModel::class.java)
    }

    private var stopSearchFragment = StopSearchFragment.newInstance()

    private fun addStopSearchFragment()
    {
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, stopSearchFragment).addToBackStack(null).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Listens for request signals from the TripPlanFragment and creates a StopSearchFragment for the appropriate data
        supportFragmentManager.apply {
            setFragmentResultListener(TripPlanFragment.ORIGIN_SEARCH_REQUEST, this@TripSearchActivity) { _, _ ->
                stopSearchFragment.searchKey = ORIGIN_SEARCH_KEY
                addStopSearchFragment()
            }
            setFragmentResultListener(TripPlanFragment.DESTINATION_SEARCH_REQUEST, this@TripSearchActivity) { _, _ ->
                stopSearchFragment.searchKey = DESTINATION_SEARCH_KEY
                addStopSearchFragment()
            }
        }
    }
}