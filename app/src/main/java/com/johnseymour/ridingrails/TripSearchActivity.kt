package com.johnseymour.ridingrails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

const val ORIGIN_SEARCH_KEY = "origin"
const val DESTINATION_SEARCH_KEY = "destination"
private const val STOP_SEARCH_FRAGMENT_BASE_NAME = "_stop_search_fragment"

class TripSearchActivity : AppCompatActivity()
{
    private fun addStopSearchFragment(searchKey: String)
    {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, StopSearchFragment.newInstance(searchKey))
            .addToBackStack(searchKey + STOP_SEARCH_FRAGMENT_BASE_NAME)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Listens for request signals from the TripPlanFragment and creates a StopSearchFragment for the appropriate data
        supportFragmentManager.apply {
            setFragmentResultListener(TripPlanFragment.ORIGIN_SEARCH_REQUEST, this@TripSearchActivity) { _, _ ->
                addStopSearchFragment(ORIGIN_SEARCH_KEY)
            }
            setFragmentResultListener(TripPlanFragment.DESTINATION_SEARCH_REQUEST, this@TripSearchActivity) { _, _ ->
                addStopSearchFragment(DESTINATION_SEARCH_KEY)
            }
        }
    }
}