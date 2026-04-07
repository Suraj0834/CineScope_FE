package com.cinescope.app.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.R
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
        setupValidation()
        observeViewModel()
        animateEntrance()
    }
    
    private fun setupViews() {
        binding.btnRegister.setOnClickListener {
            validateAndRegister()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }

        // Hide keyboard when touching outside
        binding.root.setOnClickListener {
            currentFocus?.clearFocus()
        }
    }

    /**
     * Setup real-time validation
     */
    private fun setupValidation() {
        // Name validation
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val name = s.toString().trim()
                when {
                    name.isEmpty() -> binding.tilName.error = null
                    name.length < 2 -> binding.tilName.error = getString(R.string.name_too_short)
                    else -> binding.tilName.error = null
                }
            }
        })

        // Email validation
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString().trim()
                when {
                    email.isEmpty() -> binding.tilEmail.error = null
                    !email.isValidEmail() -> binding.tilEmail.error = getString(R.string.invalid_email_format)
                    else -> binding.tilEmail.error = null
                }
            }
        })

        // Password validation with strength indicator
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                when {
                    password.isEmpty() -> {
                        binding.tilPassword.error = null
                        binding.passwordStrengthLayout.gone()
                    }
                    !password.isValidPassword() -> {
                        binding.tilPassword.error = getString(R.string.password_min_length)
                        updatePasswordStrength(password)
                    }
                    else -> {
                        binding.tilPassword.error = null
                        updatePasswordStrength(password)
                    }
                }
            }
        })

        // Confirm password validation
        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = binding.etPassword.text.toString()
                val confirmPassword = s.toString()
                when {
                    confirmPassword.isEmpty() -> binding.tilConfirmPassword.error = null
                    password != confirmPassword -> binding.tilConfirmPassword.error = getString(R.string.passwords_dont_match)
                    else -> binding.tilConfirmPassword.error = null
                }
            }
        })
    }

    /**
     * Update password strength indicator
     */
    private fun updatePasswordStrength(password: String) {
        if (password.isEmpty()) {
            binding.passwordStrengthLayout.gone()
            return
        }

        binding.passwordStrengthLayout.visible()

        val strength = calculatePasswordStrength(password)
        binding.tvPasswordStrength.apply {
            when (strength) {
                0, 1 -> {
                    text = getString(R.string.password_weak)
                    setTextColor(ContextCompat.getColor(this@RegisterActivity, R.color.md_theme_error))
                }
                2 -> {
                    text = getString(R.string.password_medium)
                    setTextColor(Color.parseColor("#FFA500")) // Orange
                }
                else -> {
                    text = getString(R.string.password_strong)
                    setTextColor(Color.parseColor("#4CAF50")) // Green
                }
            }
        }
    }

    /**
     * Calculate password strength (0-3)
     */
    private fun calculatePasswordStrength(password: String): Int {
        var strength = 0
        if (password.length >= 8) strength++
        if (password.any { it.isDigit() }) strength++
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) strength++
        if (password.any { !it.isLetterOrDigit() }) strength++
        return strength.coerceAtMost(3)
    }

    /**
     * Animate entrance of UI elements
     */
    private fun animateEntrance() {
        // Logo animation
        binding.logoCard.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .start()
        }

        // Title animation
        binding.tvTitle.apply {
            alpha = 0f
            translationY = -30f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(400)
                .start()
        }

        // Subtitle animation
        binding.tvSubtitle.apply {
            alpha = 0f
            translationY = -20f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(400)
                .start()
        }

        // Form card animation
        binding.formCard.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(500)
                .start()
        }
    }

    /**
     * Shake animation for error
     */
    private fun shakeView(view: android.view.View) {
        val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        animator.duration = 500
        animator.start()
    }
    
    private fun validateAndRegister() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Clear previous errors
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        when {
            name.isEmpty() -> {
                binding.tilName.error = getString(R.string.name_required)
                binding.etName.requestFocus()
                shakeView(binding.tilName)
                return
            }
            name.length < 2 -> {
                binding.tilName.error = getString(R.string.name_too_short)
                binding.etName.requestFocus()
                shakeView(binding.tilName)
                return
            }
            email.isEmpty() -> {
                binding.tilEmail.error = getString(R.string.email_required)
                binding.etEmail.requestFocus()
                shakeView(binding.tilEmail)
                return
            }
            !email.isValidEmail() -> {
                binding.tilEmail.error = getString(R.string.invalid_email_format)
                binding.etEmail.requestFocus()
                shakeView(binding.tilEmail)
                return
            }
            password.isEmpty() -> {
                binding.tilPassword.error = getString(R.string.password_required)
                binding.etPassword.requestFocus()
                shakeView(binding.tilPassword)
                return
            }
            !password.isValidPassword() -> {
                binding.tilPassword.error = getString(R.string.password_min_length)
                binding.etPassword.requestFocus()
                shakeView(binding.tilPassword)
                return
            }
            confirmPassword.isEmpty() -> {
                binding.tilConfirmPassword.error = getString(R.string.password_required)
                binding.etConfirmPassword.requestFocus()
                shakeView(binding.tilConfirmPassword)
                return
            }
            password != confirmPassword -> {
                binding.tilConfirmPassword.error = getString(R.string.passwords_dont_match)
                binding.etConfirmPassword.requestFocus()
                shakeView(binding.tilConfirmPassword)
                return
            }
            !binding.cbTerms.isChecked -> {
                shakeView(binding.cbTerms)
                Snackbar.make(binding.root, getString(R.string.accept_terms), Snackbar.LENGTH_LONG).show()
                return
            }
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.register(name, email, password)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is RegisterState.Idle -> {
                        binding.loadingOverlay.gone()
                        binding.btnRegister.isEnabled = true
                    }
                    is RegisterState.Loading -> {
                        binding.loadingOverlay.visible()
                        binding.btnRegister.isEnabled = false
                    }
                    is RegisterState.Success -> {
                        binding.loadingOverlay.gone()
                        preferenceManager.saveToken(state.token)
                        preferenceManager.saveUserInfo(state.user.name, state.user.email)
                        Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                        navigateToHome()
                    }
                    is RegisterState.Error -> {
                        binding.loadingOverlay.gone()
                        binding.btnRegister.isEnabled = true
                        shakeView(binding.formCard)
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
