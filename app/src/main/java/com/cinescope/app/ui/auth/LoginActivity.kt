package com.cinescope.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.databinding.ActivityLoginBinding
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.util.NetworkUtils
import com.cinescope.app.util.PreferenceManager
import com.cinescope.app.util.gone
import com.cinescope.app.util.isValidEmail
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        // Check if already logged in
        if (preferenceManager.isLoggedIn()) {
            navigateToHome()
            return
        }
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            validateAndLogin()
        }
        
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }
    
    private fun validateAndLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        
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
            password.isEmpty() -> {
                binding.etPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                return
            }
            password.length < 6 -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
                return
            }
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
            return
        }
        
        viewModel.login(email, password)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        binding.progressBar.gone()
                        binding.btnLogin.isEnabled = true
                    }
                    is LoginState.Loading -> {
                        binding.progressBar.visible()
                        binding.btnLogin.isEnabled = false
                    }
                    is LoginState.Success -> {
                        binding.progressBar.gone()
                        preferenceManager.saveToken(state.token)
                        preferenceManager.saveUserInfo(state.user.name, state.user.email)
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                    is LoginState.Error -> {
                        binding.progressBar.gone()
                        binding.btnLogin.isEnabled = true
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
