package com.cinescope.app.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.R
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
        setupEmailValidation()
        observeViewModel()
        animateEntrance()
    }

    private fun setupViews() {
        // Back button
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Back to login button
        binding.btnBackToLogin.setOnClickListener {
            finish()
        }

        // Submit button
        binding.btnSubmit.setOnClickListener {
            validateAndSendOtp()
        }
    }

    /**
     * Setup real-time email validation
     */
    private fun setupEmailValidation() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()

                when {
                    email.isEmpty() -> {
                        binding.tilEmail.error = null
                        binding.tilEmail.isErrorEnabled = false
                    }
                    !email.isValidEmail() -> {
                        binding.tilEmail.error = getString(R.string.invalid_email_format)
                    }
                    else -> {
                        binding.tilEmail.error = null
                        binding.tilEmail.isErrorEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Validate and send OTP
     */
    private fun validateAndSendOtp() {
        val email = binding.etEmail.text.toString().trim()

        // Clear any existing errors
        binding.tilEmail.error = null

        when {
            email.isEmpty() -> {
                binding.tilEmail.error = getString(R.string.email_required)
                binding.etEmail.requestFocus()
                shakeView(binding.inputCard)
                return
            }
            !email.isValidEmail() -> {
                binding.tilEmail.error = getString(R.string.invalid_email_format)
                binding.etEmail.requestFocus()
                shakeView(binding.inputCard)
                return
            }
        }

        // Check network connectivity
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(
                binding.root,
                getString(R.string.no_internet_connection),
                Snackbar.LENGTH_LONG
            ).setAction("Settings") {
                // Open network settings
            }.show()
            return
        }

        // Send OTP request
        viewModel.sendOtp(email)
    }

    /**
     * Observe ViewModel state changes
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.forgotPasswordState.collect { state ->
                when (state) {
                    is ForgotPasswordState.Idle -> {
                        hideLoading()
                        binding.btnSubmit.isEnabled = true
                    }
                    is ForgotPasswordState.Loading -> {
                        showLoading()
                        binding.btnSubmit.isEnabled = false
                    }
                    is ForgotPasswordState.Success -> {
                        hideLoading()
                        showSuccessState(state.message)

                        // Navigate to reset password after showing success
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                            intent.putExtra("email", binding.etEmail.text.toString().trim())
                            startActivity(intent)
                            finish()
                        }, 2000)
                    }
                    is ForgotPasswordState.Error -> {
                        hideLoading()
                        binding.btnSubmit.isEnabled = true
                        showErrorState(state.message)
                    }
                }
            }
        }
    }

    /**
     * Show loading overlay
     */
    private fun showLoading() {
        binding.loadingOverlay.visible()
        binding.progressBar.visible()
    }

    /**
     * Hide loading overlay
     */
    private fun hideLoading() {
        binding.loadingOverlay.gone()
        binding.progressBar.gone()
    }

    /**
     * Show success state with animation
     */
    private fun showSuccessState(message: String) {
        binding.successCard.visible()
        binding.tvSuccessMessage.text = message

        // Animate success card
        binding.successCard.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    /**
     * Show error state
     */
    private fun showErrorState(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                validateAndSendOtp()
            }
            .setBackgroundTint(ContextCompat.getColor(this, R.color.md_theme_error))
            .setTextColor(ContextCompat.getColor(this, R.color.md_theme_onError))
            .show()

        shakeView(binding.inputCard)
    }

    /**
     * Shake animation for error feedback
     */
    private fun shakeView(view: View) {
        ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f).apply {
            duration = 500
            start()
        }
    }

    /**
     * Animate entrance of elements
     */
    private fun animateEntrance() {
        // Animate illustration card
        binding.illustrationCard.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Animate title
        binding.tvTitle.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(400)
                .start()
        }

        // Animate description
        binding.tvDescription.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(400)
                .start()
        }

        // Animate step indicator
        binding.stepIndicator.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(400)
                .start()
        }

        // Animate input card
        binding.inputCard.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(400)
                .start()
        }

        // Animate submit button
        binding.btnSubmit.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(500)
                .setDuration(400)
                .start()
        }
    }
}
