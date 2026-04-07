package com.cinescope.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.cinescope.app.databinding.ActivitySplashBinding
import com.cinescope.app.ui.auth.LoginActivity
import com.cinescope.app.ui.home.HomeActivity
import com.cinescope.app.util.PreferenceManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private lateinit var prefManager: PreferenceManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PreferenceManager(this)

        // Start entrance animations
        animateEntrance()

        // Animate loading dots
        animateProgressDots()

        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2500)
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
    }

    /**
     * Animate loading dots in sequence
     */
    private fun animateProgressDots() {
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
        handler.postDelayed(runnable, 800) // Start after initial animations
    }
    
    private fun navigateToNextScreen() {
        val token = prefManager.getToken()
        
        val intent = if (token != null) {
            // User is logged in, go to Home
            Intent(this, HomeActivity::class.java)
        } else {
            // User not logged in, go to Login
            Intent(this, LoginActivity::class.java)
        }
        
        startActivity(intent)
        finish()
        
        // Add smooth transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
