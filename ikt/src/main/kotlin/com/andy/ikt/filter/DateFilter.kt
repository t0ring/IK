package com.andy.ikt.filter

data class DateFilter(
        var date: String?,
        var month: String? = null,
        var dayOfWeek: String? = null,
        var dayOfMonth: String? = null,
        var isSelected: Boolean = false
)