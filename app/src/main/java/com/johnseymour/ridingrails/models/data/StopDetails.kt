package com.johnseymour.ridingrails.models.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StopDetails(
    val id: Int,
    val name: String,
    val disassembledName: String,
    val travelModes: List<TravelMode>
) : Parcelable