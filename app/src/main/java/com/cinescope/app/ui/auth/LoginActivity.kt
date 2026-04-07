package com.cinescope.app.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cinescope.app.R
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
        setupValidation()
        setupRememberMe()
        observeViewModel()
        animateEntrance()
    }

    private fun setupViews() {
        // Login button
        binding.btnLogin.setOnClickListener {
            hideKeyboard()
            validateAndLogin()
        }

        // Register link
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Forgot password link
        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        // Biometric login button (optional)
        binding.btnBiometric.setOnClickListener {
            // TODO: Implement biometric authentication
            Snackbar.make(binding.root, "Biometric login - Coming soon!", Snackbar.LENGTH_SHORT).show()
        }

        // Social login (optional)
        binding.btnGoogleLogin.setOnClickListener {
            // TODO: Implement Google Sign In
            Snackbar.make(binding.root, "Google Sign In - Coming soon!", Snackbar.LENGTH_SHORT).show()
        }

        // Enter key to login
        binding.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                validateAndLogin()
                true
            } else {
                false
            }
        }
    }

    /**
     * Setup real-time validation
     */
    private fun setupValidation() {
        // Email validation
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

        // Password validation
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()

                when {
                    password.isEmpty() -> {
                        binding.tilPassword.error = null
                        binding.tilPassword.isErrorEnabled = false
                    }
                    password.length < 6 -> {
                        binding.tilPassword.error = getString(R.string.password_min_length)
                    }
                    else -> {
                        binding.tilPassword.error = null
                        binding.tilPassword.isErrorEnabled = false
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Setup remember me functionality
     */
    private fun setupRememberMe() {
        // TODO: Implement remember me persistence
        // For now, just keep the checkbox functional
        binding.cbRememberMe.setOnCheckedChangeListener { _, isChecked ->
            // Remember me preference would be saved here
        }
    }

    /**
     * Validate and login
     */
    private fun validateAndLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Clear any existing errors
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        when {
            email.isEmpty() -> {
                binding.tilEmail.error = getString(R.string.email_required)
                binding.etEmail.requestFocus()
                shakeView(binding.loginFormCard)
                return
            }
            !email.isValidEmail() -> {
                binding.tilEmail.error = getString(R.string.invalid_email_format)
                binding.etEmail.requestFocus()
                shakeView(binding.loginFormCard)
                return
            }
            password.isEmpty() -> {
                binding.tilPassword.error = "Password is required"
                binding.etPassword.requestFocus()
                shakeView(binding.loginFormCard)
                return
            }
            password.length < 6 -> {
                binding.tilPassword.error = getString(R.string.password_min_length)
                binding.etPassword.requestFocus()
                shakeView(binding.loginFormCard)
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

        // TODO: Save email if remember me is checked
        // if (binding.cbRememberMe.isChecked) {
        //     preferenceManager.saveEmail(email)
        // }

        // Perform login
        viewModel.login(email, password)
    }

    /**
     * Observe ViewModel state changes
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Idle -> {
                        hideLoading()
                        binding.btnLogin.isEnabled = true
                    }
                    is LoginState.Loading -> {
                        showLoading()
                        binding.btnLogin.isEnabled = false
                    }
                    is LoginState.Success -> {
                        hideLoading()
                        preferenceManager.saveToken(state.token)
                        preferenceManager.saveUserInfo(state.user.name, state.user.email)

                        // Show success message
                        Snackbar.make(
                            binding.root,
                            getString(R.string.login_success),
                            Snackbar.LENGTH_SHORT
                        ).show()

                        // Navigate to home
                        navigateToHome()
                    }
                    is LoginState.Error -> {
                        hideLoading()
                        binding.btnLogin.isEnabled = true
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
     * Show error state
     */
    private fun showErrorState(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                validateAndLogin()
            }
            .setBackgroundTint(ContextCompat.getColor(this, R.color.md_theme_error))
            .setTextColor(ContextCompat.getColor(this, R.color.md_theme_onError))
            .show()

        shakeView(binding.loginFormCard)
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
        // Animate branding card
        binding.brandingCard.apply {
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

        // Animate welcome text
        binding.tvWelcome.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(400)
                .start()
        }

        // Animate subtitle
        binding.tvSubtitle.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(400)
                .start()
        }

        // Animate login form
        binding.loginFormCard.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(300)
                .setDuration(400)
                .start()
        }

        // Animate login button
        binding.btnLogin.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(400)
                .start()
        }
    }

    /**
     * Hide keyboard
     */
    private fun hideKeyboard() {
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    /**
     * Navigate to home screen
     */
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
