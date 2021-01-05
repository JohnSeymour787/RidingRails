package com.johnseymour.ridingrails.models.data

import android.os.Parcelable
import com.johnseymour.ridingrails.R
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TripLeg(
    val tripCode: Int,
    val duration: Int,
    val lineName: String,
    val origin: PlatformDetails,
    val destination: PlatformDetails,
    val stopSequence: List<PlatformDetails>
): Parcelable
{
    @IgnoredOnParcel
    val lineColor = when (lineName)
    {
        "T1" -> R.color.T1
        "T2" -> R.color.T2
        "T3" -> R.color.T3
        "T4" -> R.color.T4
        "T5" -> R.color.T5
        "T6" -> R.color.T6
        "T7" -> R.color.T7
        "T8" -> R.color.T8
        "T9" -> R.color.T9
        "M"  -> R.color.M
        else -> R.color.colorWhiteText
    }
}