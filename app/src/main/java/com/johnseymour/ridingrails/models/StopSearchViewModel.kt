package com.johnseymour.ridingrails.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import com.johnseymour.ridingrails.apisupport.models.StatusData
import com.johnseymour.ridingrails.models.data.StopDetails

class StopSearchViewModel : ViewModel()
{
    private var searchString = ""
    lateinit var searchList: LiveData<StatusData<List<StopDetails>>>

    fun searchStops(searchTerm: String)
    {
        searchString = searchTerm
        searchList = NetworkRepository.getSingleStopDetails(searchString)
    }
}