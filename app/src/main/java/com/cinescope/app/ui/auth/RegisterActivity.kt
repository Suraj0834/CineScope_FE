package com.cinescope.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.databinding.ActivityRegisterBinding
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.util.NetworkUtils
import com.cinescope.app.util.PreferenceManager
import com.cinescope.app.util.gone
import com.cinescope.app.util.isValidEmail
import com.cinescope.app.util.isValidPassword
import com.cinescope.app.util.visible
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var preferenceManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        preferenceManager = PreferenceManager(this)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        
        setupViews()
        observeViewModel()
    }
    
    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun validateAndRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        
        when {
            name.isEmpty() -> {
                binding.etName.error = "Name is required"
                binding.etName.requestFocus()
                return
            }
            name.length < 2 -> {
                binding.etName.error = "Name must be at least 2 characters"
                binding.etName.requestFocus()
                return
            }
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
            !password.isValidPassword() -> {
                binding.etPassword.error = "Password must be at least 6 characters"
                binding.etPassword.requestFocus()
                return
            }
        }
        
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, "No internet connection", Snackbar.LENGTH_LONG).show()
            return
        }
        
        viewModel.register(name, email, password)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterState.Idle -> {
                        binding.progressBar.gone()
                        binding.btnRegister.isEnabled = true
                    }
                    is RegisterState.Loading -> {
                        binding.progressBar.visible()
                        binding.btnRegister.isEnabled = false
                    }
                    is RegisterState.Success -> {
                        binding.progressBar.gone()
                        preferenceManager.saveToken(state.token)
                        preferenceManager.saveUserInfo(state.user.name, state.user.email)
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                    is RegisterState.Error -> {
                        binding.progressBar.gone()
                        binding.btnRegister.isEnabled = true
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
