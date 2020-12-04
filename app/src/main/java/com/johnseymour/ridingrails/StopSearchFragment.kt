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
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.johnseymour.ridingrails.apisupport.models.Status
import com.johnseymour.ridingrails.models.StopSearchListAdapter
import com.johnseymour.ridingrails.models.StopSearchViewModel
import com.johnseymour.ridingrails.models.data.StopDetails
import kotlinx.android.synthetic.main.stop_search_fragment.*
import java.util.*

class StopSearchFragment(var searchKey: String? = null) : Fragment()
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
    private val resultListAdapter = StopSearchListAdapter(listOf(), ::stopSelected)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.stop_search_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(StopSearchViewModel::class.java)

        //Trigger the fragment listening for the search key, but don't have any data to pass back
        //Should cause the parent fragment to remove this fragment as this is now returning
        activity?.onBackPressedDispatcher?.addCallback(this) {
            val key = searchKey ?: ORIGIN_SEARCH_KEY
            setFragmentResult(key, bundleOf())
        }

        //Make the keyboard appear with the search input in focus
        searchInput.requestFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT)

        searchInput.setOnEditorActionListener { _, actionID, _ ->
            //If the search button is pressed, clear the focus and hide the keyboard
            if (actionID == EditorInfo.IME_ACTION_SEARCH)
            {
                lowerKeyboard()
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
                //Clear the adapter's list
                resultListAdapter.stops = listOf()
            }
        }
        stopDetailsList.adapter = resultListAdapter
        stopDetailsList.layoutManager = LinearLayoutManager(context)
    }

    private fun lowerKeyboard()
    {
        searchInput.clearFocus()
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(searchInput.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onPause()
    {
        super.onPause()
        lowerKeyboard()
        resultListAdapter.stops = listOf()
        searchInput.text?.clear()
    }

    private fun stopSelected(stop: StopDetails)
    {
        val key = searchKey ?: ORIGIN_SEARCH_KEY
        setFragmentResult(key, bundleOf(key to stop))
    }

    private fun setDataObserver()
    {
        viewModel.searchList.observe(viewLifecycleOwner) {
            //TODO() All error reporting here
            when (it.status)
            {
                Status.Success ->
                {
                    resultListAdapter.apply {
                        stops = it?.data ?: listOf()
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }

companion object
{
        private const val API_CALL_DELAY_MS = 100L
        fun newInstance(searchKey: String) = StopSearchFragment(searchKey)
}

}