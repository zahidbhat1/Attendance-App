package com.raybit.testhilt.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.raybit.testhilt.R

import com.raybit.testhilt.ui.home_model.DateModel
import java.util.*

class DateAdapter(private val arrDates: List<DateModel>) :
    RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(arrDates[position])
    }

    override fun getItemCount(): Int {
        return arrDates.size
    }

    inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDay)
        private val dayTextView: TextView = itemView.findViewById(R.id.tvDayName)

        fun bind(dateModel: DateModel) {
            dateTextView.text = dateModel.dayDate
            dayTextView.text = dateModel.dayName

            // Check if the date is the current date
            val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            if (dateModel.dayDate.toInt() == today) {
                // Set background drawable for the current date
                val bgDrawable = ContextCompat.getDrawable(itemView.context, R.drawable.current_date_box)
                itemView.background = bgDrawable

                // Set text color to white for the current date
                dateTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
                dayTextView.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.white))
            } else {
                // Reset background for other dates
                val bgd = ContextCompat.getDrawable(itemView.context, R.drawable.date_box)
                itemView.background = bgd

                // Reset text color for other dates
                dateTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                dayTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }
}