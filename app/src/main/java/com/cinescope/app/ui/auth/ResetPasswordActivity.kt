package com.cinescope.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.databinding.ActivityResetPasswordBinding
import com.cinescope.app.util.NetworkUtils
import com.cinescope.app.util.gone
import com.cinescope.app.util.isValidPassword
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ResetPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel
    private var email: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        email = intent.getStringExtra("email") ?: ""
        viewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnReset.setOnClickListener {
            validateAndReset()
        }
    }
    
    private fun validateAndReset() {
        val otp = binding.etCode.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString()
        
        when {
            otp.isEmpty() -> {
                binding.etCode.error = "OTP is required"
                binding.etCode.requestFocus()
                return
            }
            otp.length != 6 -> {
                binding.etCode.error = "OTP must be 6 digits"
                binding.etCode.requestFocus()
                return
            }
            newPassword.isEmpty() -> {
                binding.etNewPassword.error = "New password is required"
                binding.etNewPassword.requestFocus()
                return
            }
            !newPassword.isValidPassword() -> {
                binding.etNewPassword.error = "Password must be at least 6 characters"
                binding.etNewPassword.requestFocus()
                return
            }
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
            return
        }
        
        viewModel.resetPassword(email, otp, newPassword)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.resetPasswordState.collect { state ->
                when (state) {
                    is ResetPasswordState.Idle -> {
                        binding.progressBar.gone()
                        binding.btnReset.isEnabled = true
                    }
                    is ResetPasswordState.Loading -> {
                        binding.progressBar.visible()
                        binding.btnReset.isEnabled = false
                    }
                    is ResetPasswordState.Success -> {
                        binding.progressBar.gone()
                        Toast.makeText(this@ResetPasswordActivity, "Password reset successful!", Toast.LENGTH_SHORT).show()
                        
                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    is ResetPasswordState.Error -> {
                        binding.progressBar.gone()
                        binding.btnReset.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
