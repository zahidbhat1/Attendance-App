package com.raybit.testhilt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.raybit.testhilt.AuthViewModel
import com.raybit.testhilt.R
import com.raybit.testhilt.Utils.NetworkResult
import com.raybit.testhilt.Utils.TokenManager
import com.raybit.testhilt.api.UserAPI
import com.raybit.testhilt.databinding.FragmentLoginBinding
import com.raybit.testhilt.di.models.SignInReq
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentLogin : Fragment() {

    private lateinit var binding: FragmentLoginBinding // Declare binding at the class level
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var userAPI: UserAPI

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using view binding
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            val validationResult = validateUserInput()
            if (validationResult.first) {
                // Use lifecycleScope.launch to call loginUser
                lifecycleScope.launch {
                    authViewModel.loginUser(getUserRequest())
                }
            } else {
                binding.txtError.text = validationResult.second
            }
        }

        authViewModel.signInRes.observe(viewLifecycleOwner, Observer { result ->
            binding.progressBar.isVisible = false
            when (result) {
                is NetworkResult.Success<*> -> {
                    tokenManager.saveToken(result.data!!.data.toString())
                   Toast.makeText(requireContext(),"login sucess",Toast.LENGTH_LONG).show()
                    val navController = findNavController()
                    navController.navigate(R.id.action_fragmentLogin_to_fragmentHome)


                }
                is NetworkResult.Error<*> -> {
                    binding.txtError.text = result.message
                }
                is NetworkResult.Loading<*> -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    private fun getUserRequest(): SignInReq {
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return SignInReq(emailAddress, password)

    }
    // git change example

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getUserRequest()
        return authViewModel.validateCredentials(
            userRequest.email,
            userRequest.password,
            true
        )
    }
}
