package com.raybit.testhilt.dialogs

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.raybit.testhilt.AuthViewModel
import com.raybit.testhilt.Utils.TokenManager
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CheckInConfirmationDialog(
    private val context: Context,
    private val tokenManager: TokenManager,
    private val authViewModel: AuthViewModel,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onCheckInConfirmed: (checkinTime: String) -> Unit
) {
    fun show() {
        AlertDialog.Builder(context)
            .setTitle("Confirm Check-in")
            .setMessage("Are you sure you want to check in?")
            .setPositiveButton("Confirm") { _, _ ->
                lifecycleScope.launch {
                    val token = tokenManager.getToken()
                    if (token != null) {
                        try {
                            val checkinTime = authViewModel.checkInUser() // Assuming checkInUser() returns a String directly
                            Toast.makeText(
                                context,
                                "Check-in complete",
                                Toast.LENGTH_SHORT
                            ).show()
                            onCheckInConfirmed.invoke(checkinTime.toString()) // Pass checkinTime back
                        } catch (e: HttpException) {
                            Toast.makeText(
                                context,
                                "Failed to check in: ${e.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Token not found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}