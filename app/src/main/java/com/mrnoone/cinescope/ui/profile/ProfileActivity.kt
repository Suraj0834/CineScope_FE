package com.mrnoone.cinescope.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mrnoone.cinescope.R
import com.mrnoone.cinescope.ailang.AiLang
import com.mrnoone.cinescope.databinding.ActivityProfileBinding
import com.mrnoone.cinescope.ui.auth.LoginActivity
import com.mrnoone.cinescope.ui.favorites.FavoritesActivity
import com.mrnoone.cinescope.ui.home.HomeActivity
import com.mrnoone.cinescope.ui.watchlist.WatchlistActivity
import com.mrnoone.cinescope.util.PreferenceManager
import com.mrnoone.cinescope.util.gone
import com.mrnoone.cinescope.util.visible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var prefManager: PreferenceManager
    
    private val languages = mapOf(
        "English" to "en",
        "Hindi" to "hi",
        "Telugu" to "te",
        "Tamil" to "ta",
        "French" to "fr",
        "Spanish" to "es",
        "German" to "de",
        "Chinese" to "zh",
        "Japanese" to "ja",
        "Korean" to "ko",
        "Russian" to "ru",
        "Arabic" to "ar"
    )

    private var isFirstResume = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        prefManager = PreferenceManager(this)
        
        setupToolbar()
        setupViews()
        setupLanguageSpinner()
        setupBottomNavigation()
        observeViewModel()
        applyTranslations() // Apply translations to UI
        
        viewModel.loadProfile()

        // Listen for language changes to refresh UI - update text instead of recreating
        AiLang.addListener {
            applyTranslations()
        }
    }

    override fun onResume() {
        super.onResume()
        // Skip first resume since onCreate already loads the data
        if (!isFirstResume) {
            android.util.Log.d("ProfileActivity", "onResume - reloading profile")
            viewModel.loadProfile()
        }
        isFirstResume = false
    }

    private fun applyTranslations() {
        // Apply AiLang translations to all text views
        binding.apply {
            // Toolbar title is set in setupToolbar

            // Profile section labels
            tvWatchlistLabel.text = AiLang.t("watchlist")
            tvFavoritesLabel.text = AiLang.t("favorites")

            // Settings section
            tvSettingsLabel.text = AiLang.t("preferences")
            tvNotificationsLabel.text = AiLang.t("notifications")
            tvDarkModeLabel.text = AiLang.t("dark_mode")

            // Language section
            tvLanguageLabel.text = AiLang.t("language")
            btnApplyLanguage.text = AiLang.t("apply_language")

            // Buttons
            btnEditProfile.text = AiLang.t("edit_profile")
            btnLogout.text = AiLang.t("logout")
        }
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = AiLang.t("profile") // Use translated title
        }
    }
    
    private fun setupViews() {
        binding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        binding.btnEditProfile.setOnClickListener {
            Snackbar.make(binding.root, AiLang.t("edit_profile") + " coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnSettings.setOnClickListener {
            Snackbar.make(binding.root, "Settings", Snackbar.LENGTH_SHORT).show()
        }

        binding.cardWatchlist.setOnClickListener {
            startActivity(Intent(this, WatchlistActivity::class.java))
        }

        binding.cardFavorites.setOnClickListener {
            startActivity(Intent(this, FavoritesActivity::class.java))
        }

        binding.layoutNotifications.setOnClickListener {
            binding.switchNotifications.toggle()
        }

        binding.layoutDarkMode.setOnClickListener {
            binding.switchDarkMode.toggle()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            prefManager.setNotificationsEnabled(isChecked)
            Snackbar.make(
                binding.root,
                if (isChecked) "Notifications enabled" else "Notifications disabled",
                Snackbar.LENGTH_SHORT
            ).show()
        }

        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            prefManager.setDarkModeEnabled(isChecked)
        }

        // Language Apply Button
        binding.btnApplyLanguage.setOnClickListener {
            val customLang = binding.etCustomLang.text.toString().trim()
            if (customLang.isNotEmpty()) {
                changeLanguage(customLang)
            } else {
                val selectedLangName = binding.spinnerLanguage.selectedItem.toString()
                val langCode = languages[selectedLangName] ?: "en"
                changeLanguage(langCode)
            }
        }

        // About Section
        binding.layoutPrivacy.setOnClickListener {
            Snackbar.make(binding.root, "Privacy Policy - Coming soon", Snackbar.LENGTH_SHORT).show()
        }

        binding.layoutTerms.setOnClickListener {
            Snackbar.make(binding.root, "Terms & Conditions - Coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupLanguageSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages.keys.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        // Set current selection
        val currentLangCode = AiLang.getLanguage()
        val currentLangName = languages.entries.find { it.value == currentLangCode }?.key ?: "English"
        val position = adapter.getPosition(currentLangName)
        binding.spinnerLanguage.setSelection(position)
    }

    private fun changeLanguage(langCode: String) {
        if (langCode == AiLang.getLanguage()) return

        Snackbar.make(binding.root, "Translating to $langCode...", Snackbar.LENGTH_SHORT).show()
        AiLang.setLanguage(langCode)
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.selectedItemId = R.id.navigation_profile
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_watchlist -> {
                    startActivity(Intent(this, WatchlistActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_profile -> true
                else -> false
            }
        }
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        // binding.progressBar.visible() // No progress bar in this layout, maybe add one or just wait
                    }
                    is ProfileState.Success -> {
                        displayProfile(state)
                    }
                    is ProfileState.Error -> {
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                            .setAction("Retry") {
                                viewModel.loadProfile()
                            }
                            .show()
                    }
                    is ProfileState.Unauthorized -> {
                        android.util.Log.w("ProfileActivity", "Unauthorized - clearing token and redirecting to login")
                        Snackbar.make(binding.root, "Session expired. Please login again.", Snackbar.LENGTH_SHORT).show()
                        // Auto logout after a short delay
                        binding.root.postDelayed({
                            logout()
                        }, 1000)
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun displayProfile(state: ProfileState.Success) {
        binding.apply {
            tvUserName.text = state.user.name
            tvUserEmail.text = state.user.email

            tvWatchlistCount.text = state.watchlist.size.toString()
            tvFavoritesCount.text = state.favorites.size.toString()

            // Calculate watch time (mock - 2 hours per movie)
            val totalMovies = state.watchlist.size + state.favorites.size
            val watchHours = totalMovies * 2
            tvWatchTime.text = "${watchHours}h"

            // Load user preferences
            binding.switchNotifications.isChecked = prefManager.isNotificationsEnabled()
            binding.switchDarkMode.isChecked = prefManager.isDarkModeEnabled()
        }
    }
    
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.logout_confirmation))
            .setPositiveButton("Yes") { _, _ ->
                logout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun logout() {
        prefManager.clearToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
