package com.johnseymour.ridingrails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.johnseymour.ridingrails.apisupport.NetworkRepository
import java.util.concurrent.Executors

class TripSearchActivity : AppCompatActivity()
{
    val BACKGROUND = Executors.newFixedThreadPool(2)


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkController = NetworkRepository()
        networkController.planTrip("sydney central", "circular")

        BACKGROUND.submit {
            Thread.sleep(10000)
            //networkController.planTrip("central", "leonards")
        }
    }
}