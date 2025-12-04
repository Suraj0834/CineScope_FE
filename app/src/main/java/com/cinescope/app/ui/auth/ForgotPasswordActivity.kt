package com.cinescope.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.databinding.ActivityForgotPasswordBinding
import com.cinescope.app.util.NetworkUtils
import com.cinescope.app.util.gone
import com.cinescope.app.util.isValidEmail
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var viewModel: ForgotPasswordViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ForgotPasswordViewModel::class.java]
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
        
        binding.btnSubmit.setOnClickListener {
            validateAndSendOtp()
        }
    }
    
    private fun validateAndSendOtp() {
        val email = binding.etEmail.text.toString().trim()
        
        when {
            email.isEmpty() -> {
                binding.etEmail.error = "Email is required"
                binding.etEmail.requestFocus()
                return
            }
            !email.isValidEmail() -> {
                binding.etEmail.error = "Invalid email format"
                binding.etEmail.requestFocus()
                return
            }
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
            return
        }
        
        viewModel.sendOtp(email)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forgotPasswordState.collect { state ->
                when (state) {
                    is ForgotPasswordState.Idle -> {
                        binding.progressBar.gone()
                        binding.btnSubmit.isEnabled = true
                    }
                    is ForgotPasswordState.Loading -> {
                        binding.progressBar.visible()
                        binding.btnSubmit.isEnabled = false
                    }
                    is ForgotPasswordState.Success -> {
                        binding.progressBar.gone()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        
                        val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        intent.putExtra("email", binding.etEmail.text.toString().trim())
                        startActivity(intent)
                        finish()
                    }
                    is ForgotPasswordState.Error -> {
                        binding.progressBar.gone()
                        binding.btnSubmit.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
