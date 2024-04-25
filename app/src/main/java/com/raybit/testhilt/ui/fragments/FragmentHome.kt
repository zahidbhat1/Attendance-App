package com.raybit.testhilt.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.raybit.testhilt.AuthViewModel
import com.raybit.testhilt.R
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.Utils.TokenManager
import com.raybit.testhilt.databinding.FragmentHomeBinding
import com.raybit.testhilt.ui.adapters.ActivityAdapter
import com.raybit.testhilt.ui.adapters.DateAdapter
import com.raybit.testhilt.ui.home_model.ActivityModel
import com.raybit.testhilt.ui.home_model.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!



    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel by viewModels<AuthViewModel>()



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

        val currentIndex = dateList.indexOfFirst { it.dayDate == currentDate.dayDate }

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
        checkinList.add(checkin2)
        val checkin4 = ActivityModel("19:03:2024", "10:30 am", "On late")



        rvCheckIn.adapter = ActivityAdapter(checkinList)
        var seekBar = binding.seekBar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    val progress = seekBar.progress
                    if (progress >= 95) {
                        // If progress is over 95%, set progress to 100%
                        seekBar.progress = 100

                        // Launch a coroutine to trigger the check-in process
                        lifecycleScope.launch {

                            val token = tokenManager.getToken()
                            if (token != null) {
                                authViewModel.checkInUser()


                                Toast.makeText(
                                    requireContext(),
                                    "Check-in complete",
                                    Toast.LENGTH_SHORT
                                ).show()

                                seekBar.setBackgroundResource(R.drawable.custom_seekbar_checkout)
                                seekBar.progress=0

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Token not found",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                    } else {
                        seekBar.progress = 0
                    }
                }

            }
        })
        authViewModel.attendanceData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val attendanceData = result.data?.data // Access the "data" object within the response

                    // Check if the "attendance" array exists and is not empty
                    if (attendanceData != null && attendanceData.attendance.isNotEmpty()) {
                        // Iterate over each attendance record in the "attendance" array
                        val activityList = attendanceData.attendance.map { attendanceRecord ->
                            // Extract the date and time from the 'in' field of each record
                            val dateTime = attendanceRecord.checkinTime
                            val date = extractDate(dateTime)
                            val time = extractTime(dateTime)
                            val status=null

                            // Create an ActivityModel instance for each record
                            ActivityModel(date = date, time = time,status=null)
                        }

                        // Update the RecyclerView adapter with the list of ActivityModel instances
                        // Assuming that rvCheckIn is your RecyclerView for displaying attendance records
                        rvCheckIn.adapter = ActivityAdapter(activityList)
                    }
                }
                is NetworkResult.Error -> {
                    // Handle the error here
                    Log.e("Attendance", "Failed to fetch attendance: ${result.message ?: "Unknown error"}")
                }
                is NetworkResult.Loading -> {
                    // Handle loading state if needed
                }
            }
        })



        binding.tvTodayAttendence.setOnClickListener{
    fetchAttendance()
}

    }

    private fun fetchAttendance() {

        authViewModel.fetchAttendance()
    }
    fun extractDate(dateTime: String): String {
        // Assuming the date is in the format "yyyy-MM-ddTHH:mm:ss.SSSZ"
        // Extract the date part before the 'T'
        return dateTime.substringBefore("T")
    }

    fun extractTime(dateTime: String): String {
        // Assuming the date is in the format "yyyy-MM-ddTHH:mm:ss.SSSZ"
        // Extract the time part after the 'T'
        return dateTime.substringAfter("T").substringBeforeLast(":")
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
