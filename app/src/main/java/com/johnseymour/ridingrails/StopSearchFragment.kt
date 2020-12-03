package com.johnseymour.ridingrails

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.StopSearchListAdapter
import com.johnseymour.ridingrails.models.StopSearchViewModel
import kotlinx.android.synthetic.main.stop_search_fragment.*
import java.util.*

class StopSearchFragment : Fragment()
{
    private var delayTimer = Timer()
    private inner class APIDelayTask(val searchTerm: String): TimerTask()
    {
        override fun run()
        {
            viewModel.searchStops(searchTerm)
            activity?.runOnUiThread {
                setDataObserver()
            }
        }
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

        //Make the keyboard appear with the search input in focus
        searchInput.requestFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)

        searchInput.setOnEditorActionListener { _, actionID, _ ->
            //If the search button is pressed, clear the focus and hide the keyboard
            if (actionID == EditorInfo.IME_ACTION_SEARCH)
            {
                searchInput.clearFocus()
                (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(searchInput.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        searchInput.doOnTextChanged { text, _, _, _ ->
            //Remove cancelled tasks from the timer and make a new one
            delayTimer.cancel()
            delayTimer = Timer()

            //API usually doesn't return much for searches with less than 3 characters
            if (text?.length ?:0 > 2)
            {
                //Schedule the API call to occur a short time later, if the user hasn't typed another character
                delayTimer.schedule(APIDelayTask(text.toString()), API_CALL_DELAY_MS)
            }
            else
            {
                //Clear the list
                stopDetailsList.adapter = StopSearchListAdapter(listOf())
            }
        }

        stopDetailsList.layoutManager = LinearLayoutManager(context)
    }


    private fun setDataObserver()
    {
        viewModel.searchList.observe(viewLifecycleOwner) {
            //TODO() All error reporting here
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

    companion object
    {
        private const val API_CALL_DELAY_MS = 100L

        fun newInstance() = StopSearchFragment()
    }
}