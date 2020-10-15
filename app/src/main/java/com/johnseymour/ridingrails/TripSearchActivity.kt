package com.johnseymour.ridingrails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.johnseymour.ridingrails.apisupport.NetworkRepository

class TripSearchActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkRepository().getStopDetails("st leonards")
    }
}