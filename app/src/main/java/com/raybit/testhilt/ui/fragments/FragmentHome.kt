
package com.raybit.testhilt.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.service.autofill.UserData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.raybit.testhilt.AuthViewModel
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.Utils.TokenManager
import com.raybit.testhilt.databinding.FragmentHomeBinding
import com.raybit.testhilt.dialogs.CheckInConfirmationDialog
import com.raybit.testhilt.models.login_models.LoginResponse
import com.raybit.testhilt.models.login_models.Profile
import com.raybit.testhilt.ui.adapters.ActivityAdapter
import com.raybit.testhilt.ui.adapters.DateAdapter
import com.raybit.testhilt.ui.home_model.ActivityModel
import com.raybit.testhilt.ui.home_model.CheckInResponse
import com.raybit.testhilt.ui.home_model.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import com.bumptech.glide.Glide
import com.raybit.testhilt.Utils.Constants
import com.raybit.testhilt.models.attendance.UserId
import com.raybit.testhilt.models.break_models.BreakModel
import kotlinx.coroutines.launch
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

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profile = tokenManager.getProfile()

// Check if the profile data is not null
        if (profile != null) {
            // Extract the first name, last name, and image URL from the profile
            val firstName = profile.firstName
            val lastName = profile.lastName
            val imageUrl = profile.image
            val userId = profile.userId


            // Concatenate the first name and last name to form the full name
            val fullName = "$firstName $lastName"

            // Set the full name to the appropriate TextView
            binding.tvName.text = fullName

            // Load the image using Glide or any other image loading library
            Glide.with(requireContext())
                .load(imageUrl)
                .into(binding.imgStudent)
        }


        // Rest of your onViewCreated code...


        val userId = profile?.userId

        // Fetch the status for the current user
        val isCheckedIn = tokenManager.getCheckinStatus(userId.toString())
        val isCheckedOut = tokenManager.getCheckoutStatus(userId.toString())

        // Update the UI based on the fetched status
        if (isCheckedIn) {
            binding.tvCheckin.text = "Checked In"
            binding.tvCheckin.isEnabled = false
        }

        if (isCheckedOut) {
            binding.tvCheckout.text = "Checked Out"
            binding.tvCheckout.isEnabled = false
        }

        // Clear status for the previous user if any
        tokenManager.clearStatus(userId.toString())


        var checkinTime: String? = null
        fetchAttendance()


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

        binding.tvCheckin.setOnClickListener {
            if (!isCheckedIn) { // Check if already checked in
                CheckInConfirmationDialog(
                    requireContext(),
                    tokenManager,
                    authViewModel,
                    viewLifecycleOwner.lifecycleScope
                ) {
                    binding.tvCheckin.text = "Checked In"
                    binding.tvCheckin.isEnabled = false
                    tokenManager.saveCheckinStatus(
                        userId.toString(),
                        true
                    ) // Save the checked-in status
                }.show()
            }
        }

        binding.tvCheckout.setOnClickListener {
            showCheckoutConfirmationDialog()
            tokenManager.saveCheckoutStatus(userId.toString(), true)


        }





        authViewModel.attendanceData.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is NetworkResult.Success -> {
                    val attendanceData = result.data?.data

                    if (attendanceData != null && attendanceData.attendance.isNotEmpty()) {
                        val latestAttendanceRecord = attendanceData.attendance.firstOrNull()

                        latestAttendanceRecord?.let {
//

                            val checkinTimeStr = it.checkinTime
                            val formattedCheckinTime = extractTime(checkinTimeStr)
                            binding.tvChkInTime.text = formattedCheckinTime
                            val checkoutTimeStr = it.checkoutTime
                            if (checkoutTimeStr != null) {
                                val formattedCheckoutTime = extractTime(checkoutTimeStr)
                                binding.tvChkoutTime2.text = formattedCheckoutTime
                            }


                            val checkinTimeDate = SimpleDateFormat(
                                "yyyy-MM-dd'T'HH:mm:ss",
                                Locale.getDefault()
                            ).parse(checkinTimeStr)


                            val calendar = Calendar.getInstance()
                            calendar.time = checkinTimeDate
                            val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)


                            val status = if (hourOfDay >= 10) "Late" else "On time"
                            binding.tvstatus.text = status


                        }

                        val activityList =
                            attendanceData.attendance.mapNotNull { attendanceRecord ->

                                val dateTime = attendanceRecord.checkinTime
                                if (!dateTime.isNullOrEmpty()) {
                                    val date = extractDate(dateTime)
                                    val time = extractTime(dateTime)

                                    val status = "on time "

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
                    // Handle loading state if needed
                }
            }
        })

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


    private fun showCheckoutConfirmationDialog() {
        val checkoutDialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirm Checkout")
            .setMessage("Are you sure you want to checkout?")
            .setPositiveButton("Confirm") { dialog, _ ->
                dialog.dismiss()
                authViewModel.checkInUser()

                binding.tvCheckout.apply {
                    text = "Checked Out"
                    isEnabled = false
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        checkoutDialog.show()
    }






    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}