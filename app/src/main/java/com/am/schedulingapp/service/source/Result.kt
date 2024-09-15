package com.am.schedulingapp.service.source

data class Result<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Result<T> =
            Result(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: String): Result<T> =
            Result(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Result<T> =
            Result(status = Status.LOADING, data = data, message = null)
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}