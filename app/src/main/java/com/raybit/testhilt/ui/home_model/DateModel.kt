package com.raybit.testhilt.ui.home_model

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

data class DateModel(val dayName: String, val dayDate: String, val isCurrentDate: Boolean = false)
object DateUtils {
    fun getCurrentDate(): DateModel {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd")
        val dayDate = dateFormat.format(cal.time)
        val dayName = SimpleDateFormat("EEE").format(cal.time)
        return DateModel(dayName, dayDate, isCurrentDate = true)
    }

    fun getDateList(): List<DateModel> {
        val cal = Calendar.getInstance()
        val currentDate = cal.get(Calendar.DAY_OF_MONTH)
        val dateList = mutableListOf<DateModel>()
        val dateFormat = SimpleDateFormat("dd")
        val dayFormat = SimpleDateFormat("EEE")

        for (i in currentDate..currentDate + 29) {
            cal.set(Calendar.DAY_OF_MONTH, i)
            val dayName = dayFormat.format(cal.time)
            val dayDate = dateFormat.format(cal.time)
            dateList.add(DateModel(dayName, dayDate))
        }
        return dateList
    }
}