<p align="center">
  <img src="https://img.icons8.com/fluency/96/cinema-.png" alt="CineScope Logo" width="120"/>
</p>

<h1 align="center">🎬 CineScope</h1>

<p align="center">
  <strong>AI-Powered Movie Discovery Android Application</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android"/>
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin"/>
  <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white" alt="Material 3"/>
  <img src="https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white" alt="Gemini AI"/>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Min%20SDK-26-green?style=flat-square" alt="Min SDK"/>
  <img src="https://img.shields.io/badge/Target%20SDK-35-blue?style=flat-square" alt="Target SDK"/>
  <img src="https://img.shields.io/badge/Version-1.0-orange?style=flat-square" alt="Version"/>
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" alt="License"/>
</p>

---

## 📖 Overview

**CineScope** is a modern Android application for movie discovery, featuring AI-powered summaries, real-time streaming availability, and a unique **multi-language translation framework** called **AiLang**. Built with Kotlin and Material Design 3, it offers a premium cinematic experience.

### ✨ Key Highlights

- 🤖 **AI Movie Summaries** - Get intelligent movie insights powered by Google Gemini
- 🌐 **AiLang Framework** - Revolutionary AI-powered real-time language translation
- 🎥 **Streaming Availability** - Find where to watch movies in India
- 👤 **Actor Profiles** - Real Wikipedia photos and filmography
- 📱 **Modern UI/UX** - Material Design 3 with smooth animations

---

## 🖼️ Screenshots

<p align="center">
  <table>
    <tr>
      <td align="center"><b>🏠 Home</b></td>
      <td align="center"><b>🔍 Search</b></td>
      <td align="center"><b>🎬 Details</b></td>
      <td align="center"><b>🤖 AI Summary</b></td>
    </tr>
    <tr>
      <td><img src="https://via.placeholder.com/200x400/1a1a2e/e94560?text=Home" width="200"/></td>
      <td><img src="https://via.placeholder.com/200x400/1a1a2e/e94560?text=Search" width="200"/></td>
      <td><img src="https://via.placeholder.com/200x400/1a1a2e/e94560?text=Details" width="200"/></td>
      <td><img src="https://via.placeholder.com/200x400/1a1a2e/e94560?text=AI+Summary" width="200"/></td>
    </tr>
  </table>
</p>

---

## 🏗️ Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CINESCOPE ARCHITECTURE                            │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        📱 ANDROID APPLICATION                         │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  │   │
│  │  │    Home     │  │   Search    │  │   Details   │  │   Profile   │  │   │
│  │  │  Activity   │  │  Activity   │  │  Activity   │  │  Activity   │  │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  │   │
│  │         │                │                │                │         │   │
│  │         └────────────────┴────────────────┴────────────────┘         │   │
│  │                                  │                                    │   │
│  │                          ┌──────┴──────┐                             │   │
│  │                          │  ViewModel  │                             │   │
│  │                          │    Layer    │                             │   │
│  │                          └──────┬──────┘                             │   │
│  │                                 │                                     │   │
│  │         ┌───────────────────────┼───────────────────────┐            │   │
│  │         │                       │                       │            │   │
│  │  ┌──────┴──────┐        ┌──────┴──────┐        ┌──────┴──────┐      │   │
│  │  │ Repository  │        │   AiLang    │        │   ApiClient │      │   │
│  │  │   Layer     │        │  Framework  │        │   (Retrofit)│      │   │
│  │  └──────┬──────┘        └──────┬──────┘        └──────┬──────┘      │   │
│  │         │                      │                      │              │   │
│  └─────────┼──────────────────────┼──────────────────────┼──────────────┘   │
│            │                      │                      │                   │
│            ▼                      ▼                      ▼                   │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────────────┐  │
│  │  SharedPrefs    │    │  Google Gemini  │    │   CineScope Backend     │  │
│  │  (Local Cache)  │    │   Translation   │    │   (Node.js + Express)   │  │
│  └─────────────────┘    └─────────────────┘    └───────────┬─────────────┘  │
│                                                            │                 │
│                         ┌──────────────────────────────────┼──────────┐      │
│                         │                                  │          │      │
│                         ▼                                  ▼          ▼      │
│               ┌─────────────────┐              ┌──────────────┐ ┌──────────┐│
│               │    MongoDB      │              │   Trakt.tv   │ │  OMDb    ││
│               │   (User Data)   │              │   (Movies)   │ │ (Posters)││
│               └─────────────────┘              └──────────────┘ └──────────┘│
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Data Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              DATA FLOW DIAGRAM                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│   USER ACTION                                                                │
│       │                                                                      │
│       ▼                                                                      │
│   ┌───────────┐    ┌───────────┐    ┌───────────┐    ┌───────────┐         │
│   │   View    │───▶│ ViewModel │───▶│Repository │───▶│ ApiClient │         │
│   │ (Activity)│    │  (State)  │    │  (Data)   │    │ (Network) │         │
│   └───────────┘    └───────────┘    └───────────┘    └─────┬─────┘         │
│        ▲                                                    │               │
│        │                                                    ▼               │
│        │           ┌────────────────────────────────────────────────┐      │
│        │           │              BACKEND SERVER                     │      │
│        │           │  ┌─────────┐  ┌─────────┐  ┌─────────────────┐ │      │
│        │           │  │  Auth   │  │ Movies  │  │     Gemini      │ │      │
│        │           │  │ Routes  │  │ Routes  │  │ (AI Summaries)  │ │      │
│        │           │  └────┬────┘  └────┬────┘  └────────┬────────┘ │      │
│        │           │       │            │                │          │      │
│        │           │       ▼            ▼                ▼          │      │
│        │           │  ┌─────────────────────────────────────────┐  │      │
│        │           │  │           EXTERNAL APIS                  │  │      │
│        │           │  │  • Trakt.tv (Movie Database)            │  │      │
│        │           │  │  • OMDb (Posters & Ratings)             │  │      │
│        │           │  │  • Watchmode (Streaming Sources)        │  │      │
│        │           │  │  • Wikipedia (Actor Photos)             │  │      │
│        │           │  └─────────────────────────────────────────┘  │      │
│        │           └────────────────────────────────────────────────┘      │
│        │                                    │                               │
│        │                                    ▼                               │
│        │                            ┌─────────────┐                        │
│        └────────────────────────────│   Response  │                        │
│              (Update UI)            │    JSON     │                        │
│                                     └─────────────┘                        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🌐 AiLang Framework - Deep Dive

### What is AiLang?

**AiLang** (AI Language) is a custom-built, AI-powered translation framework that enables **real-time multilingual support** without pre-stored translations. Unlike traditional i18n solutions, AiLang uses Google Gemini AI to translate UI strings dynamically.

### 🔥 Why AiLang is Revolutionary

| Traditional i18n | AiLang Framework |
|------------------|------------------|
| Requires pre-translated strings | Translates on-demand using AI |
| Limited to supported languages | Supports 50+ languages instantly |
| Manual updates for each language | Zero maintenance for translations |
| Large app size with string files | Minimal footprint |
| Static translations | Context-aware smart translations |

### AiLang Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         AILANG FRAMEWORK ARCHITECTURE                        │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                           APP LAYER                                  │    │
│  │                                                                      │    │
│  │    Activity/Fragment                                                 │    │
│  │          │                                                           │    │
│  │          ▼                                                           │    │
│  │    ┌─────────────┐                                                  │    │
│  │    │  AiLang     │  ◄── Entry Point                                 │    │
│  │    │  .init()    │      Initialize with context                     │    │
│  │    │  .t("key")  │      Get translated string                       │    │
│  │    └──────┬──────┘                                                  │    │
│  │           │                                                          │    │
│  └───────────┼──────────────────────────────────────────────────────────┘    │
│              │                                                               │
│              ▼                                                               │
│  ┌─────────────────────────────────────────────────────────────────────┐    │
│  │                      AILANG CORE COMPONENTS                          │    │
│  │                                                                      │    │
│  │  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐  │    │
│  │  │  LanguageStore  │    │ TranslationCache│    │TranslationEngine│  │    │
│  │  │                 │    │                 │    │                 │  │    │
│  │  │ • currentLang   │    │ • HashMap<K,V>  │    │ • Gemini API    │  │    │
│  │  │ • preferences   │    │ • LRU eviction  │    │ • Batch process │  │    │
│  │  │ • listeners     │    │ • Persistence   │    │ • JSON parsing  │  │    │
│  │  └────────┬────────┘    └────────┬────────┘    └────────┬────────┘  │    │
│  │           │                      │                      │           │    │
│  │           └──────────────────────┼──────────────────────┘           │    │
│  │                                  │                                   │    │
│  │                                  ▼                                   │    │
│  │                    ┌─────────────────────────┐                      │    │
│  │                    │    Translation Flow     │                      │    │
│  │                    │                         │                      │    │
│  │                    │  1. Check cache         │                      │    │
│  │                    │  2. If miss → API call  │                      │    │
│  │                    │  3. Parse response      │                      │    │
│  │                    │  4. Update cache        │                      │    │
│  │                    │  5. Notify listeners    │                      │    │
│  │                    └─────────────────────────┘                      │    │
│  │                                                                      │    │
│  └──────────────────────────────────────────────────────────────────────┘    │
│                                                                              │
│                                  │                                           │
│                                  ▼                                           │
│  ┌──────────────────────────────────────────────────────────────────────┐   │
│  │                        GOOGLE GEMINI AI                               │   │
│  │                                                                       │   │
│  │   Prompt: "Translate these UI strings to Hindi: {...}"               │   │
│  │                                                                       │   │
│  │   Response: {"home": "होम", "search": "खोजें", "profile": "प्रोफ़ाइल"}│   │
│  │                                                                       │   │
│  └──────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

### Supported Languages (50+)

```
🇮🇳 Hindi       🇪🇸 Spanish     🇫🇷 French      🇩🇪 German      🇮🇹 Italian
🇵🇹 Portuguese  🇷🇺 Russian     🇯🇵 Japanese    🇰🇷 Korean      🇨🇳 Chinese
🇸🇦 Arabic      🇹🇷 Turkish     🇳🇱 Dutch       🇵🇱 Polish      🇸🇪 Swedish
🇹🇭 Thai        🇻🇳 Vietnamese  🇮🇩 Indonesian  🇲🇾 Malay       🇵🇭 Filipino
🇧🇩 Bengali     🇮🇳 Tamil       🇮🇳 Telugu      🇮🇳 Marathi     🇮🇳 Gujarati
🇮🇳 Kannada     🇮🇳 Malayalam   🇮🇳 Punjabi     🇮🇳 Urdu        🇬🇷 Greek
🇨🇿 Czech       🇷🇴 Romanian    🇭🇺 Hungarian   🇫🇮 Finnish     🇳🇴 Norwegian
🇩🇰 Danish      🇺🇦 Ukrainian   🇮🇱 Hebrew      🇵🇰 Urdu        ... and more!
```

### Usage Example

```kotlin
// Initialize AiLang in Application class
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AiLang.init(this)
    }
}

// Use in Activity
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get translated strings
        binding.tvTitle.text = AiLang.t("home_title")
        binding.btnSearch.text = AiLang.t("search")
        
        // Change language dynamically
        AiLang.setLanguage("hi") // Switch to Hindi
    }
}
```

---

## 📁 Project Structure

```
CineScope_FE/
│
├── 📱 app/
│   ├── src/main/
│   │   ├── java/com/cinescope/app/
│   │   │   │
│   │   │   ├── 🌐 ailang/              # AiLang Translation Framework
│   │   │   │   ├── AiLang.kt           # Main entry point
│   │   │   │   ├── LanguageStore.kt    # Language preferences
│   │   │   │   ├── TranslationCache.kt # Caching layer
│   │   │   │   └── TranslationEngine.kt# Gemini API integration
│   │   │   │
│   │   │   ├── 📊 data/                # Data Layer
│   │   │   │   ├── api/
│   │   │   │   │   ├── ApiClient.kt    # Retrofit client
│   │   │   │   │   └── ApiService.kt   # API endpoints
│   │   │   │   ├── model/
│   │   │   │   │   ├── Movie.kt        # Movie data class
│   │   │   │   │   ├── MovieDetail.kt  # Detailed movie info
│   │   │   │   │   ├── Person.kt       # Actor/Cast data
│   │   │   │   │   └── User.kt         # User authentication
│   │   │   │   └── repository/
│   │   │   │       ├── MovieRepository.kt
│   │   │   │       └── AuthRepository.kt
│   │   │   │
│   │   │   ├── 🎨 ui/                  # UI Layer
│   │   │   │   ├── auth/               # Login/Register/Forgot
│   │   │   │   ├── home/               # Home screen
│   │   │   │   ├── details/            # Movie details
│   │   │   │   ├── person/             # Actor details
│   │   │   │   ├── profile/            # User profile
│   │   │   │   ├── search/             # Search movies
│   │   │   │   ├── watchlist/          # User watchlist
│   │   │   │   ├── chat/               # AI Chat
│   │   │   │   ├── splash/             # Splash screen
│   │   │   │   └── common/             # Shared adapters
│   │   │   │
│   │   │   ├── 🔧 util/                # Utilities
│   │   │   │   ├── Constants.kt        # App constants
│   │   │   │   └── Extensions.kt       # Kotlin extensions
│   │   │   │
│   │   │   └── App.kt                  # Application class
│   │   │
│   │   └── res/
│   │       ├── layout/                 # XML layouts
│   │       ├── values/                 # Strings, colors, themes
│   │       ├── drawable/               # Icons & drawables
│   │       └── anim/                   # Animations
│   │
│   └── build.gradle.kts                # App dependencies
│
├── 📋 gradle/
│   └── libs.versions.toml              # Version catalog
│
├── 🔧 build.gradle.kts                 # Project config
├── 📝 settings.gradle.kts              # Settings
├── 🔒 local.properties                 # API keys (git-ignored)
└── 📖 README.md                        # This file
```

---

## 🚀 Features

### 🎬 Movie Discovery
- **Trending Movies** - Real-time trending content from Trakt.tv
- **Popular Movies** - Most popular movies globally
- **Search** - Instant search with debouncing
- **Infinite Scroll** - Seamless pagination

### 🤖 AI Features
- **Smart Summaries** - AI-generated movie summaries
- **Language Translation** - Real-time UI translation (AiLang)
- **Contextual Insights** - Intelligent movie analysis

### 📱 User Features
- **Authentication** - Register/Login/Forgot Password
- **Watchlist** - Save movies to watch later
- **Favorites** - Mark favorite movies
- **Profile** - User profile management

### 🎥 Movie Details
- **Full Details** - Runtime, rating, genres, overview
- **Cast & Crew** - Real actor photos from Wikipedia
- **Streaming Sources** - Where to watch (India region)
- **Similar Movies** - Recommendations
- **Trailers** - YouTube integration

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 1.9+ |
| **UI Framework** | Material Design 3 |
| **Architecture** | MVVM + Repository Pattern |
| **Networking** | Retrofit 2 + OkHttp 4 |
| **Image Loading** | Glide 4 |
| **Async** | Kotlin Coroutines + Flow |
| **DI** | Manual (ViewModelProvider) |
| **AI** | Google Gemini API |
| **Backend** | Node.js + Express ([CineScope_BE](https://github.com/Suraj0834/CineScope_BE)) |

---

## ⚙️ Setup & Installation

### Prerequisites

- Android Studio Hedgehog or later
- JDK 11+
- Android SDK 26+
- Google Gemini API key

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Suraj0834/CineScope_FE.git
   cd CineScope_FE
   ```

2. **Configure API Key**
   
   Create/edit `local.properties` in root directory:
   ```properties
   sdk.dir=/path/to/your/android/sdk
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
   
   > ⚠️ Get your Gemini API key from [Google AI Studio](https://aistudio.google.com/)

3. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to cloned directory

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🔗 Related Repositories

| Repository | Description |
|------------|-------------|
| [CineScope_BE](https://github.com/Suraj0834/CineScope_BE) | Backend API (Node.js + Express + MongoDB) |

---

## 📄 API Reference

The app connects to **CineScope Backend** hosted on Render:

```
Base URL: https://cinescope-be.onrender.com
```

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/movies/trending` | GET | Get trending movies |
| `/api/movies/popular` | GET | Get popular movies |
| `/api/movies/search` | GET | Search movies |
| `/api/movies/:imdbId` | GET | Get movie details |
| `/api/person/:id` | GET | Get actor details |
| `/api/auth/register` | POST | User registration |
| `/api/auth/login` | POST | User login |
| `/api/gemini/summarize` | POST | AI movie summary |

---

## 👨‍💻 Author

<p align="center">
  <img src="https://github.com/Suraj0834.png" width="100" style="border-radius: 50%"/>
</p>

<p align="center">
  <b>Suraj</b><br/>
  <a href="https://github.com/Suraj0834">GitHub</a> •
  <a href="mailto:suraj6202k@gmail.com">Email</a>
</p>

---

## 📜 License

```
MIT License

Copyright (c) 2025 Suraj

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

<p align="center">
  <b>⭐ Star this repo if you find it helpful!</b>
</p>

<p align="center">
  Made with ❤️ using Kotlin & AI
</p>
