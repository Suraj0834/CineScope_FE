# CineScope AI - Android App

A production-ready Android application for discovering movies, getting AI-powered summaries, and managing your watchlist.

## 🚀 Setup Instructions

### 1. Configure Backend URL

Open `app/src/main/java/com/cinescope/app/util/Constants.kt` and update the `BASE_URL`:

```kotlin
const val BASE_URL = "http://YOUR_BACKEND_URL:5000/"
```

**For local development:**
- Android Emulator: `http://10.0.2.2:5000/`
- Physical Device (same network): `http://192.168.x.x:5000/`
- Production: `https://your-backend-domain.com/`

### 2. Required Gradle Dependencies

Add these dependencies to your `app/build.gradle`:

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'androidx.core:core-ktx:1.10.1'
    
    // AndroidX
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.0'
    
    // Material Design
    implementation 'com.google.android.material:material:1.9.0'
    
    // Lifecycle & ViewModel
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.6.1'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.1'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1'
    
    // Retrofit & OkHttp
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Glide for image loading
    implementation 'com.github.bumptech.glide:glide:4.15.1'
    kapt 'com.github.bumptech.glide:compiler:4.15.1'
    
    // Encrypted SharedPreferences
    implementation 'androidx.security:security-crypto:1.1.0-alpha06'
    
    // View Binding
    buildFeatures {
        viewBinding true
    }
}
```

### 3. AndroidManifest.xml Configuration

Add these permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

Add application class:

```xml
<application
    android:name=".App"
    ...>
```

### 4. Project Configuration

In `gradle.properties`:
```
android.useAndroidX=true
android.enableJetifier=true
```

In `app/build.gradle`:
```gradle
android {
    compileSdk 33
    
    defaultConfig {
        minSdk 21
        targetSdk 33
        ...
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
}
```

## 📱 Features

- ✅ User Registration & Authentication
- ✅ Movie Search & Trending
- ✅ Movie Details with Streaming Availability (Watchmode)
- ✅ AI-Powered Movie Summaries (Google Gemini)
- ✅ Watchlist & Favorites Management
- ✅ User Profile
- ✅ Forgot Password (OTP Flow)
- ✅ Secure Token Storage (Encrypted SharedPreferences)

## 🏗️ Architecture

- **Pattern:** MVVM (Model-View-ViewModel)
- **Language:** Kotlin
- **UI:** XML Layouts (ConstraintLayout, Material Design)
- **Networking:** Retrofit + OkHttp
- **Async:** Kotlin Coroutines + StateFlow
- **Image Loading:** Glide
- **Security:** EncryptedSharedPreferences

## 📁 Project Structure

```
app/src/main/java/com/cinescope/app/
├── data/
│   ├── api/           # Retrofit service interfaces
│   ├── model/         # Data models
│   └── repository/    # Repository pattern
├── ui/
│   ├── auth/          # Login, Register, Forgot Password
│   ├── home/          # Movie list & search
│   ├── details/       # Movie details
│   ├── profile/       # User profile & lists
│   └── common/        # Base classes
├── util/              # Utilities & extensions
└── App.kt             # Application class
```

## 🔧 Running the App

### Option 1: Android Studio
1. Open the project in Android Studio
2. Update `BASE_URL` in `Constants.kt`
3. Sync Gradle files
4. Run on emulator or device

### Option 2: Command Line
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 🌐 Backend API Endpoints

The app consumes these endpoints:

**Authentication:**
- POST `/api/auth/register` - Register new user
- POST `/api/auth/login` - User login
- POST `/api/auth/forgot` - Request OTP
- POST `/api/auth/verify-otp` - Verify OTP & reset password

**Movies:**
- GET `/api/movies/search?q={query}` - Search movies
- GET `/api/movies/trending` - Get trending movies
- GET `/api/movies/{imdbId}` - Get movie details

**AI:**
- POST `/api/gemini/summarize` - Get AI movie summary (Protected)

**Profile:**
- GET `/api/profile` - Get user profile (Protected)
- PUT `/api/profile/watchlist` - Add/remove watchlist (Protected)
- PUT `/api/profile/favorites` - Add/remove favorites (Protected)
- GET `/api/profile/watchlist` - Get watchlist (Protected)
- GET `/api/profile/favorites` - Get favorites (Protected)

## 🔐 Authentication

- JWT tokens stored securely in EncryptedSharedPreferences
- Auto-attached to API requests via OkHttp interceptor
- Token persists across app sessions
- Logout clears all auth data

## ⚠️ Important Notes

1. **Backend CORS:** Ensure your backend allows requests from Android app
2. **Network Security:** For local testing with HTTP, add network security config
3. **API Keys:** Backend handles all API keys (OMDb, Gemini, Watchmode)
4. **Minimum SDK:** API 21 (Android 5.0)
5. **Target SDK:** API 33 (Android 13)

## 📝 Network Security Config (For Local HTTP Testing)

Create `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.1</domain>
    </domain-config>
</network-security-config>
```

Add to AndroidManifest.xml:
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

## 🐛 Troubleshooting

**Problem:** Network errors
- ✅ Check backend is running
- ✅ Verify `BASE_URL` is correct
- ✅ Check network permissions in manifest
- ✅ For local HTTP, configure network security

**Problem:** Can't login
- ✅ Verify backend auth endpoints working
- ✅ Check Logcat for detailed error messages
- ✅ Test backend with Postman first

**Problem:** Images not loading
- ✅ Ensure internet permission granted
- ✅ Check Glide dependency added
- ✅ Verify poster URLs are valid

## 📞 Support

For backend API testing and debugging, see:
- `backend/TESTING_REPORT.md`
- `backend/COMPLETE_TEST_RESULTS.md`
- Postman Collection: `backend/CineScope_API.postman_collection.json`

## 🎯 Development Status

✅ Backend fully tested (18/18 endpoints working)
✅ Android app structure complete
✅ Ready for development and testing

---

**Built with ❤️ using Kotlin & Material Design**
