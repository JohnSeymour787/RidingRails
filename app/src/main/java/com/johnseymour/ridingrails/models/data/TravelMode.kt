package com.johnseymour.ridingrails.models.data

enum class TravelMode(val modeID: Int)
{
    Train(1),
    LightRail(4),
    Bus(5),
    Coach(7),
    Ferry(9),
    SchoolBus(11),
    RemainOnBoard(97),
    Walking(99),
    WalkingFootpath(100),
    Bicycle(101),
    BicycleOnPT(102),
    KissRide(103),
    ParkRide(104),
    Taxi(105),
    Car(106);

    companion object
    {
        //Calling values() creates a new array each time, so cache this
        private val values by lazy { values() }
        /** Returns the corresponding TravelMode for the modeID passed. Returns null if no match **/
        fun valueOf(modeID: Int) = values.firstOrNull { it.modeID == modeID }
    }
}