package com.raybit.testhilt.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.raybit.testhilt.R
import com.raybit.testhilt.databinding.FragmentHomeBinding
import com.raybit.testhilt.ui.adapters.ActivityAdapter
import com.raybit.testhilt.ui.adapters.DateAdapter
import com.raybit.testhilt.ui.home_model.ActivityModel
import com.raybit.testhilt.ui.home_model.DateUtils

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root



}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentDate = DateUtils.getCurrentDate()
        val dateList = DateUtils.getDateList()

        // Find the index of the current date in the list
        val currentIndex = dateList.indexOfFirst { it.dayDate == currentDate.dayDate }

        // Start RecyclerView from the current date
        val rvDates = binding.rvDates

        // Ensure the next 30 days are within the dateList bounds
        val next30Days = if (currentIndex + 30 < dateList.size) {
            dateList.subList(currentIndex, currentIndex + 30)
        } else {
            dateList.subList(currentIndex, dateList.size)
        }

        rvDates.adapter = DateAdapter(next30Days)







        val rvCheckIn = binding.rvCheckIn
        val checkinList = arrayListOf<ActivityModel>()
        val checkin1 = ActivityModel("19:03:2024", "10:30 am", "On Time")
        checkinList.add(checkin1)
        val checkin2 = ActivityModel("19:03:2024", "10:30 am", "On Time")
        checkinList.add(checkin1)
        val checkin4 = ActivityModel("19:03:2024", "10:30 am", "On late")
        checkinList.add(checkin2)
        checkinList.add(checkin4)
        checkinList.add(checkin1)

        rvCheckIn.adapter = ActivityAdapter(checkinList)
        var seekBar = binding.seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                if (seekBar != null) {
                    val progress = seekBar.progress
                    if (progress >= 95) {
                        // If progress is over 95%, set progress to 100%
                        seekBar.progress = 100
                        // Display "Check-in complete" message
                        Toast.makeText(
                            requireContext(),
                            "Check-in complete",
                            Toast.LENGTH_SHORT
                        ).show()


                        seekBar.visibility=View.GONE
                    } else {
                        seekBar.progress = 0
                    }
                }
            }
        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
