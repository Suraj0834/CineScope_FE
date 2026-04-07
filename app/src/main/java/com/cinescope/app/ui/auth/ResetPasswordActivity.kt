package com.cinescope.app.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.R
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

        // Display email
        if (email.isNotEmpty()) {
            binding.tvEmail.text = email
            binding.tvEmail.visible()
        } else {
            binding.tvEmail.gone()
        }

        setupViews()
        setupValidation()
        observeViewModel()
        animateEntrance()
    }
    
    private fun setupViews() {
        binding.btnReset.setOnClickListener {
            validateAndReset()
        }

        binding.tvResendCode.setOnClickListener {
            resendCode()
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }

        binding.btnGoToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    /**
     * Setup real-time validation
     */
    private fun setupValidation() {
        // OTP validation
        binding.etCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val code = s.toString().trim()
                when {
                    code.isEmpty() -> binding.tilCode.error = null
                    code.length != 6 -> binding.tilCode.error = getString(R.string.otp_invalid)
                    else -> binding.tilCode.error = null
                }
            }
        })

        // Password validation with strength indicator
        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString()
                when {
                    password.isEmpty() -> {
                        binding.tilNewPassword.error = null
                        binding.passwordStrengthLayout.gone()
                    }
                    !password.isValidPassword() -> {
                        binding.tilNewPassword.error = getString(R.string.password_min_length)
                        updatePasswordStrength(password)
                    }
                    else -> {
                        binding.tilNewPassword.error = null
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
                val password = binding.etNewPassword.text.toString()
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
                    setTextColor(ContextCompat.getColor(this@ResetPasswordActivity, R.color.md_theme_error))
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
     * Resend verification code
     */
    private fun resendCode() {
        if (email.isEmpty()) {
            Snackbar.make(binding.root, "Email not found", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Disable button temporarily
        binding.tvResendCode.isEnabled = false
        Snackbar.make(binding.root, "Sending new code...", Snackbar.LENGTH_SHORT).show()

        // Re-enable after 30 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            binding.tvResendCode.isEnabled = true
        }, 30000)

        // TODO: Implement resend code API call
        Handler(Looper.getMainLooper()).postDelayed({
            Snackbar.make(binding.root, "New code sent to $email", Snackbar.LENGTH_LONG).show()
        }, 1000)
    }

    /**
     * Animate entrance of UI elements
     */
    private fun animateEntrance() {
        // Icon animation
        binding.iconCard.apply {
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
    
    private fun validateAndReset() {
        val otp = binding.etCode.text.toString().trim()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Clear previous errors
        binding.tilCode.error = null
        binding.tilNewPassword.error = null
        binding.tilConfirmPassword.error = null

        when {
            otp.isEmpty() -> {
                binding.tilCode.error = getString(R.string.otp_required)
                binding.etCode.requestFocus()
                shakeView(binding.tilCode)
                return
            }
            otp.length != 6 -> {
                binding.tilCode.error = getString(R.string.otp_invalid)
                binding.etCode.requestFocus()
                shakeView(binding.tilCode)
                return
            }
            newPassword.isEmpty() -> {
                binding.tilNewPassword.error = getString(R.string.new_password_required)
                binding.etNewPassword.requestFocus()
                shakeView(binding.tilNewPassword)
                return
            }
            !newPassword.isValidPassword() -> {
                binding.tilNewPassword.error = getString(R.string.password_min_length)
                binding.etNewPassword.requestFocus()
                shakeView(binding.tilNewPassword)
                return
            }
            confirmPassword.isEmpty() -> {
                binding.tilConfirmPassword.error = getString(R.string.password_required)
                binding.etConfirmPassword.requestFocus()
                shakeView(binding.tilConfirmPassword)
                return
            }
            newPassword != confirmPassword -> {
                binding.tilConfirmPassword.error = getString(R.string.passwords_dont_match)
                binding.etConfirmPassword.requestFocus()
                shakeView(binding.tilConfirmPassword)
                return
            }
        }

        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
            return
        }

        viewModel.resetPassword(email, otp, newPassword)
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.resetPasswordState.collect { state ->
                when (state) {
                    is ResetPasswordState.Idle -> {
                        binding.loadingOverlay.gone()
                        binding.btnReset.isEnabled = true
                    }
                    is ResetPasswordState.Loading -> {
                        binding.loadingOverlay.visible()
                        binding.btnReset.isEnabled = false
                    }
                    is ResetPasswordState.Success -> {
                        binding.loadingOverlay.gone()
                        showSuccessState()
                    }
                    is ResetPasswordState.Error -> {
                        binding.loadingOverlay.gone()
                        binding.btnReset.isEnabled = true
                        shakeView(binding.formCard)
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /**
     * Show success state
     */
    private fun showSuccessState() {
        binding.successOverlay.visible()

        // Animate success icon
        binding.successIconCard.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .start()
        }

        // Auto navigate after 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, 3000)
    }

    /**
     * Navigate to login screen
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
