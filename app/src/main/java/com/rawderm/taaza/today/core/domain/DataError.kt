package com.rawderm.taaza.today.core.domain

sealed interface DataError : Error {
    enum class Remote : DataError {
        TIMEOUT,
        NETWORK_ERROR,
        TOO_MANY_REQUESTS,
        SERVER_ERROR,
        SERIALIZATION_ERROR,
        UNKNOWN
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN
    }
}