package com.cinescope.app.util

object Constants {
    // Backend Server URL
    // For local testing use: http://10.0.2.2:5001 (Android emulator)
    // For real device on same network: http://192.168.x.x:5001
    // For production: https://your-backend-domain.com
    const val BASE_URL = "http://10.0.2.2:5001/"
    
    // Shared Preferences
    const val PREFS_NAME = "cinescope_prefs"
    const val KEY_TOKEN = "auth_token"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    
    // Intent Extras
    const val EXTRA_IMDB_ID = "imdb_id"  // Using IMDB ID as primary identifier
    const val EXTRA_TMDB_ID = "tmdb_id"  // Keep for backwards compatibility
    const val EXTRA_MOVIE_TITLE = "movie_title"
    const val EXTRA_PERSON_ID = "person_id"
    const val EXTRA_PERSON_NAME = "person_name"
    
    // TMDB Image Base URLs
    const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    const val POSTER_SIZE_SMALL = "w185"
    const val POSTER_SIZE_MEDIUM = "w342"
    const val POSTER_SIZE_LARGE = "w500"
    const val BACKDROP_SIZE_SMALL = "w300"
    const val BACKDROP_SIZE_LARGE = "w780"
    const val PROFILE_SIZE_SMALL = "w185"
    const val PROFILE_SIZE_LARGE = "h632"
    
    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val SCROLL_THRESHOLD = 4
    
    // Timeouts
    const val NETWORK_TIMEOUT = 30000L
    const val SEARCH_DEBOUNCE_DELAY = 500L

    // Currency Conversion
    const val USD_TO_INR_RATE = 83.0 // Approximate conversion rate
}
