package com.andy.ikt.filter

import java.io.Serializable

enum class TimetableType(val days: Int) : Serializable {
    CAMPUS(7),
    TEACHING(7),
    RESERVE(14)
}