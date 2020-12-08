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

/**@param searchKey Represents the fragment key used to indicate what data this fragment
 * will be used to search for, namely, Origin or Destination stations.**/
class StopSearchFragment(var searchKey: String? = null) : Fragment()
{
    private var delayTimer = Timer()
    private inner class APIDelayTask: TimerTask()
    {
        override fun run()
        {
            viewModel.searchStops()
            activity?.runOnUiThread {
                setDataObserver()
            }
        }
    }

    //Used on configuration change to prevent onTextChanged listener being called for first time
    //text-setting
    private var firstTextChange = true
    private lateinit var viewModel: StopSearchViewModel
    private val resultListAdapter = StopSearchListAdapter(listOf(), ::stopSelected)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.stop_search_fragment, container, false)
    }

    //If coming from a configuration change, searchInput's text value will be
    //automatically saved and restored at this point
    override fun onResume()
    {
        super.onResume()
        //Move the cursor to the end position
        searchInput.setSelection(searchInput?.text?.length ?: 0)
        //Observe the ViewModel's LiveData if it exists, avoiding the need for another API call,
        //if coming from a configuration change
        setDataObserver()
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
            //ViewModel always reflects the most recent search term
            viewModel.searchString = text?.toString() ?: ""
            //If coming from a configuration change or any first character, want to ignore this listener and
            //not make a new API request
            if (firstTextChange)
            {
                firstTextChange = false
                return@doOnTextChanged
            }
            //Remove cancelled tasks from the timer and make a new one
            delayTimer.cancel()
            delayTimer = Timer()

            //API usually doesn't return much for searches with less than 3 characters
            if (viewModel.searchString.length > 2)
            {
                //Schedule the API call to occur a short time later, if the user hasn't typed another character
                delayTimer.schedule(APIDelayTask(), API_CALL_DELAY_MS)
            }
            else
            {
                //Clear the ViewModel's LiveData
                viewModel.searchList?.removeObservers(viewLifecycleOwner)
                viewModel.searchList = null
                //Clear the adapter's list
                resultListAdapter.stops = listOf()
                resultListAdapter.notifyDataSetChanged()
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

    //Lower the keyboard
    override fun onPause()
    {
        super.onPause()
        lowerKeyboard()
    }

    private fun stopSelected(stop: StopDetails)
    {
        val key = searchKey ?: ORIGIN_SEARCH_KEY
        setFragmentResult(key, bundleOf(key to stop))
    }

    private fun setDataObserver()
    {
        viewModel.searchList?.observe(viewLifecycleOwner) {
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
        fun newInstance(searchKey: String? = null) = StopSearchFragment(searchKey)
    }
}