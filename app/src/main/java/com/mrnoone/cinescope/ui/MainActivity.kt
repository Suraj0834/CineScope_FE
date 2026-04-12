package com.mrnoone.cinescope.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.mrnoone.cinescope.R
import com.mrnoone.cinescope.databinding.ActivityMainBinding
import com.mrnoone.cinescope.ui.auth.LoginActivity
import com.mrnoone.cinescope.ui.home.HomeActivity
import com.mrnoone.cinescope.util.PreferenceManager
import com.mrnoone.cinescope.util.gone
import com.mrnoone.cinescope.util.visible

/**
 * Main/Splash Activity
 *
 * This activity serves as the initial loading/splash screen.
 * It checks authentication status and navigates to the appropriate screen.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceManager: PreferenceManager

    private val splashDuration = 2000L // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Setup error state retry button
        binding.btnRetry.setOnClickListener {
            showLoadingState()
            checkAuthAndNavigate()
        }

        // Start entrance animations
        animateEntrance()

        // Check authentication and navigate
        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, splashDuration)
    }

    /**
     * Animate entrance of UI elements
     */
    private fun animateEntrance() {
        // Animate logo card
        binding.logoCard.apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = 0.5f
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        // Animate app name
        binding.tvAppName.apply {
            alpha = 0f
            translationY = 50f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(200)
                .setDuration(500)
                .start()
        }

        // Animate tagline
        binding.tvTagline.apply {
            alpha = 0f
            translationY = 30f
            animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(500)
                .start()
        }

        // Animate loading section
        binding.loadingSection.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setStartDelay(600)
                .setDuration(500)
                .start()
        }

        // Animate bottom section
        binding.bottomSection.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setStartDelay(800)
                .setDuration(500)
                .start()
        }

        // Animate progress dots (optional)
        animateProgressDots()
    }

    /**
     * Animate loading dots in sequence
     */
    private fun animateProgressDots() {
        if (binding.progressDotsLayout.visibility == View.VISIBLE) {
            val dots = listOf(binding.dot1, binding.dot2, binding.dot3)
            var currentDot = 0

            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    // Reset all dots
                    dots.forEach { it.alpha = 0.3f }

                    // Highlight current dot
                    dots[currentDot].apply {
                        animate()
                            .alpha(1f)
                            .scaleX(1.2f)
                            .scaleY(1.2f)
                            .setDuration(300)
                            .withEndAction {
                                animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(200)
                                    .start()
                            }
                            .start()
                    }

                    currentDot = (currentDot + 1) % dots.size
                    handler.postDelayed(this, 500)
                }
            }
            handler.post(runnable)
        }
    }

    /**
     * Check authentication status and navigate
     */
    private fun checkAuthAndNavigate() {
        try {
            // Simulate a check or initialization
            val isLoggedIn = preferenceManager.isLoggedIn()

            // Navigate with fade animation
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = if (isLoggedIn) {
                    Intent(this, HomeActivity::class.java)
                } else {
                    Intent(this, LoginActivity::class.java)
                }

                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 300) // Small delay for smooth transition

        } catch (e: Exception) {
            // Show error state
            showErrorState(e.message ?: "An error occurred")
        }
    }

    /**
     * Show loading state
     */
    private fun showLoadingState() {
        binding.errorStateLayout.gone()
        binding.brandingSection.visible()
        binding.loadingSection.visible()
        binding.bottomSection.visible()
    }

    /**
     * Show error state
     */
    private fun showErrorState(message: String) {
        // Hide loading state
        binding.brandingSection.gone()
        binding.loadingSection.gone()
        binding.bottomSection.gone()

        // Show error state
        binding.errorStateLayout.visible()
        binding.tvErrorMessage.text = message

        // Animate error state entrance
        binding.errorStateLayout.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(300)
                .start()
        }
    }

    override fun onBackPressed() {
        // Prevent back button during splash
        // User should not be able to go back from splash screen
    }
}
