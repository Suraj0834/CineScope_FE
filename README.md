# 🎬 CineScope

<div align="center">
  <img src="app/src/main/res/drawable/cinescope_icon.png" alt="CineScope Logo" width="120" height="120">
  
  ### Your Ultimate Movie Companion
  
  [![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
  [![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
  [![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
  
  [Download APK](https://github.com/Suraj0834/CineScope_FE/releases) • [Report Bug](https://github.com/Suraj0834/CineScope_FE/issues) • [Request Feature](https://github.com/Suraj0834/CineScope_FE/issues)
</div>

---

## 📖 About

**CineScope** is a feature-rich Android application that brings the world of cinema to your fingertips. Discover trending movies, explore detailed information about your favorite films, manage personalized watchlists, and get AI-powered movie recommendations through an intelligent chat interface.

Built with modern Android development practices using Kotlin, MVVM architecture, and Material Design, CineScope offers a seamless and intuitive movie browsing experience with multi-language support for global audiences.

---

## ✨ Features

### 🎥 Core Features
- **Browse Movies**: Explore trending, popular, and top-rated movies
- **Advanced Search**: Search movies by title, actor, or genre
- **Smart Filtering**: Filter movies by category (Action, Comedy, Drama, Sci-Fi, Thriller, Horror, Romance)
- **Detailed Information**: View comprehensive movie details including:
  - Cast and crew information
  - Plot summaries and ratings
  - Release dates and runtime
  - Official trailers and videos
  - User reviews and ratings

### 📋 Personalization
- **Watchlist Management**: Save movies you want to watch later
- **Favorites Collection**: Curate your personal collection of favorite films
- **User Profiles**: Manage your account and preferences
- **Viewing History**: Track movies you've explored

### 🤖 AI-Powered
- **Intelligent Chat Assistant**: Get personalized movie recommendations
- **Natural Language Processing**: Ask questions in plain language
- **Context-Aware Suggestions**: Recommendations based on your preferences
- **Powered by Google Gemini AI**: Advanced AI for smart movie discovery

### 🌍 Global Experience
- **Multi-Language Support**: Available in 12+ languages
  - English, Hindi, Spanish, French, German
  - Chinese, Japanese, Korean, Russian
  - Arabic, Tamil, Telugu, and more
- **AI-Powered Translation**: Chat in your preferred language
- **Localized Content**: Movie information in multiple languages

### 🎨 User Experience
- **Material Design**: Modern, intuitive interface
- **Dark Mode**: Eye-friendly dark theme
- **Smooth Animations**: Polished transitions and interactions
- **Responsive Design**: Optimized for all screen sizes
- **Offline Caching**: Browse previously loaded content offline

### 🔒 Security & Privacy
- **Encrypted Storage**: Secure local data storage using EncryptedSharedPreferences
- **JWT Authentication**: Secure token-based authentication
- **HTTPS Communication**: All API calls encrypted with TLS/SSL
- **Privacy First**: No personal data sold to third parties
- **Account Deletion**: Easy account and data deletion process

---

## 🛠️ Tech Stack

### Architecture & Patterns
- **MVVM (Model-View-ViewModel)**: Clean architecture pattern
- **Repository Pattern**: Data abstraction layer
- **LiveData & Coroutines**: Reactive data handling
- **Dependency Injection**: Manual DI for lifecycle management

### Core Technologies
- **Language**: Kotlin 2.0.21
- **Min SDK**: 21 (Android 5.0 Lollipop)
- **Target SDK**: 35 (Android 15)
- **Compile SDK**: 35

### Libraries & Dependencies

#### UI & Design
- **Material Components**: `1.11.0` - Modern Material Design
- **ViewBinding**: Type-safe view access
- **RecyclerView**: Efficient list rendering
- **ConstraintLayout**: Flexible layouts
- **CardView**: Card-based UI components

#### Networking
- **Retrofit**: `2.9.0` - REST API client
- **OkHttp**: `4.11.0` - HTTP client
- **Gson Converter**: `2.9.0` - JSON parsing
- **Logging Interceptor**: `4.11.0` - Network debugging

#### Image Loading
- **Glide**: `4.15.1` - Efficient image loading and caching

#### Markdown Rendering
- **Markwon**: Markdown text rendering for AI chat

#### Lifecycle & Background
- **Lifecycle Components**: `2.6.1` - Android Jetpack lifecycle
- **ViewModel**: State management
- **LiveData**: Observable data holder
- **Coroutines**: `1.7.1` - Asynchronous programming

#### Security
- **Security Crypto**: `1.1.0-alpha06` - EncryptedSharedPreferences

#### Testing
- **JUnit**: `4.13.2` - Unit testing
- **Espresso**: `3.5.1` - UI testing
- **AndroidX Test**: `1.1.5` - Android testing framework

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 8 or higher
- Android SDK with API 35
- Gradle 8.x

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/Suraj0834/CineScope_FE.git
   cd CineScope_FE
   ```

2. **Set up API Keys**
   
   Create a `local.properties` file in the root directory:
   ```properties
   GEMINI_API_KEY=your_google_gemini_api_key_here
   ```
   
   Get your Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

3. **Open in Android Studio**
   - Launch Android Studio
   - Click "Open" and select the project directory
   - Wait for Gradle sync to complete

4. **Build and Run**
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```
   
   Or simply click the "Run" button in Android Studio

---

## 🏗️ Project Structure

```
CineScope/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mrnoone/cinescope/
│   │   │   │   ├── data/           # Data layer
│   │   │   │   │   ├── api/        # API service interfaces
│   │   │   │   │   ├── model/      # Data models
│   │   │   │   │   └── repository/ # Repository implementations
│   │   │   │   ├── ui/             # UI layer
│   │   │   │   │   ├── auth/       # Authentication screens
│   │   │   │   │   ├── home/       # Home screen
│   │   │   │   │   ├── details/    # Movie details
│   │   │   │   │   ├── chat/       # AI chat interface
│   │   │   │   │   ├── profile/    # User profile
│   │   │   │   │   ├── watchlist/  # Watchlist management
│   │   │   │   │   └── favorites/  # Favorites collection
│   │   │   │   └── utils/          # Utility classes
│   │   │   └── res/                # Resources
│   │   │       ├── layout/         # XML layouts
│   │   │       ├── drawable/       # Images and icons
│   │   │       ├── values/         # Strings, colors, themes
│   │   │       └── xml/            # Configuration files
│   │   └── test/                   # Unit tests
│   └── build.gradle                # App-level Gradle config
├── gradle/                         # Gradle wrapper
├── docs/                           # Documentation files
│   ├── privacy-policy.html         # Privacy policy
│   └── delete-account.html         # Account deletion guide
└── README.md                       # This file
```

---

## 🌐 Backend API

CineScope connects to a custom backend server for user authentication and data management.

- **Backend Repository**: [CineScope_BE](https://github.com/Suraj0834/CineScope_BE)
- **API Base URL**: `https://cinescope-be.onrender.com`
- **Technology**: Node.js + Express + MongoDB

### API Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `GET /api/user/profile` - Get user profile
- `POST /api/watchlist` - Add to watchlist
- `GET /api/watchlist` - Get user's watchlist
- `POST /api/favorites` - Add to favorites
- `GET /api/favorites` - Get user's favorites

### External APIs
- **TMDb API**: Movie data and information
- **Google Gemini AI**: AI-powered chat and recommendations

---

## 📱 Screenshots

> Add screenshots of your app here to showcase its features

```
Coming soon...
```

---

## 🔐 Security

CineScope takes security seriously:

- **Encrypted Local Storage**: All sensitive data stored using `EncryptedSharedPreferences`
- **Secure Authentication**: JWT-based token authentication
- **HTTPS Only**: All network communications encrypted
- **No Hardcoded Secrets**: API keys stored in `local.properties` (not in version control)
- **Password Encryption**: User passwords encrypted using bcrypt
- **Session Management**: Automatic token expiration and refresh

---

## 🌍 Privacy

- **Privacy Policy**: [View Privacy Policy](https://suraj0834.github.io/CineScope_FE/privacy-policy.html)
- **Account Deletion**: [Request Account Deletion](https://suraj0834.github.io/CineScope_FE/delete-account.html)
- **Data Collection**: We collect only essential data for app functionality
- **User Rights**: Full GDPR and CCPA compliance
- **No Third-Party Ads**: Your experience is ad-free

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m "Add some amazing feature"
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Development Guidelines
- Follow Kotlin coding conventions
- Write meaningful commit messages
- Add comments for complex logic
- Test your changes thoroughly
- Update documentation as needed

---

## 🐛 Bug Reports & Feature Requests

Found a bug or have a feature idea? Please [open an issue](https://github.com/Suraj0834/CineScope_FE/issues) with:

- **Bug Reports**: Steps to reproduce, expected behavior, actual behavior, screenshots
- **Feature Requests**: Clear description, use cases, mockups (if applicable)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Suraj**

- GitHub: [@Suraj0834](https://github.com/Suraj0834)
- Frontend Repository: [CineScope_FE](https://github.com/Suraj0834/CineScope_FE)
- Backend Repository: [CineScope_BE](https://github.com/Suraj0834/CineScope_BE)

---

## 🙏 Acknowledgments

- [TMDb](https://www.themoviedb.org/) - Movie database and API
- [Google Gemini AI](https://ai.google.dev/) - AI-powered recommendations
- [Material Design](https://material.io/) - Design guidelines
- [Android Developers](https://developer.android.com/) - Documentation and tools

---

## 📞 Support

Need help? Reach out:

- **Email**: support@cinescope.app
- **Issues**: [GitHub Issues](https://github.com/Suraj0834/CineScope_FE/issues)
- **Documentation**: [Privacy Policy](https://suraj0834.github.io/CineScope_FE/privacy-policy.html)

---

<div align="center">
  
  ### ⭐ Star this repo if you find it helpful!
  
  Made with ❤️ by Suraj
  
  © 2026 CineScope. All rights reserved.
  
</div>
