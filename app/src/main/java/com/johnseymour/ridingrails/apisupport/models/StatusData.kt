package com.johnseymour.ridingrails.apisupport.models

/**Used to wrap data with a status of its availability. Used by Network Repository to return
 * Live<StatusData> for activities to observe. When activities see the status as failed, they can
 * display an error message to the user.
 * */
data class StatusData<T>(val status: Status, val data: T?, val message: String?)
{
    companion object
    {
        fun <T> success(data: T): StatusData<T> = StatusData(Status.Success, data, null)
        fun <T> generalFailure(message: String?): StatusData<T> = StatusData(Status.UnknownError, null, message)
        fun <T> networkError(): StatusData<T> = StatusData(Status.NetworkError, null, null)
    }
}