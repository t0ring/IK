package com.andy.ikt.filter

import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.*

private const val Y_M_D = "yyyy-MM-dd"
private val DAY_OF_WEEK = arrayListOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

fun main() {
    timetableFilters(TimetableType.RESERVE)
            .map {
                it.forEach(System.out::println)
            }
            .subscribe()
}

fun timetableFilters(type: TimetableType): Observable<List<DateFilter>> {
    val result = arrayListOf<DateFilter>()
    val calendar = Calendar.getInstance()
    calendar.set(2018, 11, 28)
    val dateFormat = SimpleDateFormat(Y_M_D, Locale.CHINA)
    for (i in 0 until type.days) {
        val dateFilter = DateFilter(
                dateFormat.format(calendar.time),
                "${calendar.get(Calendar.YEAR)}.${calendar.get(Calendar.MONTH).inc()}",
                DAY_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK).dec()],
                "${calendar.get(Calendar.DAY_OF_MONTH)}",
                i == 0
        )
        result.add(dateFilter)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return Observable.just(result)
}