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
import com.bumptech.glide.Glide
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
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
                                seekBar.progress = 0

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
                    val attendanceData = result.data?.data

                    if (attendanceData != null && attendanceData.attendance.isNotEmpty()) {

                        val activityList = attendanceData.attendance.mapNotNull { attendanceRecord ->

                            val dateTime = attendanceRecord.checkinTime
                            if (!dateTime.isNullOrEmpty()) {
                                val date = extractDate(dateTime)
                                val time = extractTime(dateTime)

                                val status ="on time "

                                ActivityModel(date = date, time = time, status = status)
                            } else {
                                null // Skip null or empty dateTime values
                            }
                        }
                        rvCheckIn.adapter = ActivityAdapter(activityList)
                    }
                }

                is NetworkResult.Error -> {

                    Log.e(
                        "Attendance",
                        "Failed to fetch attendance: ${result.message ?: "Unknown error"}"
                    )
                }

                is NetworkResult.Loading -> {

                }
            }
        })




        binding.tvTodayAttendence.setOnClickListener {
            fetchAttendance()
        }

    }

    private fun fetchAttendance() {

        authViewModel.fetchAttendance()
    }

    private fun extractDate(dateTime: String?): String {
        if (dateTime.isNullOrEmpty()) {
            return "N/A" // or any default value you want to return for null or empty dateTime
        }

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        return try {
            val date = parser.parse(dateTime)
            formatter.format(date!!)
        } catch (e: ParseException) {
            "N/A" // or handle the parse exception accordingly
        }
    }

    private fun extractTime(dateTime: String?): String {
        if (dateTime.isNullOrEmpty()) {
            return "N/A" // or any default value you want to return for null or empty dateTime
        }

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            val date = parser.parse(dateTime)
            formatter.format(date!!)
        } catch (e: ParseException) {
            "N/A" // or handle the parse exception accordingly
        }
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
//git 2nd change
