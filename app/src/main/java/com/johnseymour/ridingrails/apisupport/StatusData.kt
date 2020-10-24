package com.johnseymour.ridingrails.apisupport

data class StatusData<T>(val status: Status, val data: T?, val message: String?)
{
    companion object
    {
        fun <T> success(data: T): StatusData<T> = StatusData(Status.SUCCESS, data, null)
        fun <T> error(message: String?): StatusData<T> = StatusData(Status.ERROR, null, message)
    }
}