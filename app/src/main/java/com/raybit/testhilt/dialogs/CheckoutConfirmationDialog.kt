//package com.raybit.testhilt.dialogs
//
//class CheckoutConfirmationDialog(
//    private val context: Context,
//    private val authViewModel: AuthViewModel,
//    private val lifecycleScope: LifecycleCoroutineScope,
//    private val onCheckoutConfirmed: () -> Unit
//) {
//    fun show() {
//        AlertDialog.Builder(context)
//            .setTitle("Confirm Checkout")
//            .setMessage("Are you sure you want to checkout?")
//            .setPositiveButton("Confirm") { _, _ ->
//                lifecycleScope.launch {
//                    authViewModel.checkoutUser()
//                    onCheckoutConfirmed.invoke()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//}
