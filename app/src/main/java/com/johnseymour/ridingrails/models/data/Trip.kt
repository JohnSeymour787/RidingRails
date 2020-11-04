package com.johnseymour.ridingrails.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trip(var origin: StopDetails? = null, var destination: StopDetails? = null, var favourite: Boolean = false): Parcelable
{
    fun validTrip() = (origin != null) && (destination != null)
}