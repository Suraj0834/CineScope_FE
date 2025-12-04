package com.cinescope.app.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        
        // Animate logo
        binding.ivLogo.alpha = 0f
        binding.tvAppName.alpha = 0f
        binding.tvTagline.alpha = 0f
        
        binding.ivLogo.animate()
            .alpha(1f)
            .setDuration(1000)
            .start()
        
        binding.tvAppName.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()
        
        binding.tvTagline.animate()
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(600)
            .start()
        
        // Navigate after delay
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToNextScreen()
        }, 2500)
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
