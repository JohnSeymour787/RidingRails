package com.johnseymour.ridingrails

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.johnseymour.ridingrails.models.data.TripJourney
import kotlinx.android.synthetic.main.trip_journey_detail_fragment.*

class TripJourneyDetailFragment(private val tripJourney: TripJourney) : Fragment()
{
    companion object
    {
        fun newInstance(tripJourney: TripJourney) = TripJourneyDetailFragment(tripJourney)
    }

    private lateinit var viewModel: TripJourneyDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.trip_journey_detail_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TripJourneyDetailViewModel::class.java)
        viewModel.tripJourney = tripJourney

        test.text = viewModel.tripJourney.toString()
        val cake2 = 2
    }
}