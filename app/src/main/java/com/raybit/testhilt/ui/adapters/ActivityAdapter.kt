package com.raybit.testhilt.ui.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.raybit.testhilt.R
import com.raybit.testhilt.ui.home_model.ActivityModel

class ActivityAdapter(private val checkInList: ArrayList<ActivityModel>) :
    RecyclerView.Adapter<ActivityAdapter.CheckInViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckInViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity, parent, false)
        return CheckInViewHolder(view)
    }

    override fun onBindViewHolder(holder: CheckInViewHolder, position: Int) {
        val checkIn = checkInList[position]
        holder.bind(checkIn)
    }

    override fun getItemCount(): Int {
        return checkInList.size
    }

    class CheckInViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewDate: TextView = itemView.findViewById(R.id.textView7)
        private val textViewTime: TextView = itemView.findViewById(R.id.TvCheckInTime)
        private val textViewStatus: TextView = itemView.findViewById(R.id.tvCheckInStatus)

        fun bind(checkIn: ActivityModel) {
            textViewDate.text = checkIn.date
            textViewTime.text = checkIn.time
            textViewStatus.text = checkIn.status
        }
    }
}
