# How to Setup Privacy Policy URL for Google Play Store

This guide will help you host your privacy policy on GitHub Pages and get a public URL for Google Play Store.

## Step 1: Commit and Push the Privacy Policy Files

```bash
cd "/Users/suraj/Downloads/androdi hackthon"

# Add the files
git add docs/privacy-policy.html docs/index.html PRIVACY_POLICY.md

# Commit
git commit -m "Add privacy policy for Google Play Store"

# Push to GitHub
git push origin main
```

## Step 2: Enable GitHub Pages

1. Go to your GitHub repository: https://github.com/Suraj0834/CineScope_FE

2. Click on **Settings** (top right, near the repository name)

3. Scroll down to **Pages** section in the left sidebar

4. Under "Build and deployment":
   - **Source**: Select "Deploy from a branch"
   - **Branch**: Select "main" and folder "/docs"
   - Click **Save**

5. Wait 1-2 minutes for GitHub to build your site

6. Your privacy policy will be available at:
   ```
   https://suraj0834.github.io/CineScope_FE/privacy-policy.html
   ```

## Step 3: Verify Your Privacy Policy URL

1. Open your browser and visit:
   ```
   https://suraj0834.github.io/CineScope_FE/privacy-policy.html
   ```

2. Make sure the page loads correctly with all the styling

3. If you see a 404 error, wait a few more minutes and refresh

## Step 4: Submit to Google Play Store

### 4.1 Google Play Console Setup

1. Go to [Google Play Console](https://play.google.com/console)

2. Click **Create app**

3. Fill in the required information:
   - **App name**: CineScope
   - **Default language**: English (United States)
   - **App or game**: App
   - **Free or paid**: Free

### 4.2 Store Listing

1. Go to **Store presence** → **Main store listing**

2. Fill in all required fields:

   **App name**: `CineScope`

   **Short description** (80 characters max):
   ```
   Discover movies, create watchlists, and chat with AI about your favorites
   ```

   **Full description** (4000 characters max):
   ```
   CineScope - Your Ultimate Movie Companion

   Discover, explore, and organize your favorite movies with CineScope! Whether you're a casual movie watcher or a dedicated cinephile, CineScope helps you keep track of what to watch next.

   KEY FEATURES:

   🎬 Extensive Movie Database
   - Browse trending movies and popular titles
   - Search from a vast collection of films
   - Get detailed information including cast, crew, ratings, and reviews
   - View high-quality posters and movie artwork

   📝 Personal Watchlist & Favorites
   - Create your personalized watchlist for movies you want to watch
   - Mark movies as favorites to easily find them later
   - Track your movie collection across devices
   - Never forget a recommendation again

   🤖 AI-Powered Chat Assistant
   - Ask our AI assistant about any movie
   - Get personalized recommendations based on your taste
   - Discuss plot details, cast information, and more
   - Smart responses powered by Google Gemini AI

   🌍 Multi-Language Support
   - Available in 12+ languages including English, Hindi, Spanish, French, German, and more
   - AI-powered translation for seamless experience
   - Switch languages on the fly

   🎨 Beautiful Design
   - Modern, intuitive interface
   - Dark mode support for comfortable viewing
   - Smooth animations and transitions
   - Easy navigation between sections

   🔒 Privacy & Security
   - Secure account management
   - Encrypted data storage
   - Your data is never sold to third parties
   - HTTPS encryption for all communications

   PERFECT FOR:
   - Movie enthusiasts who want to organize their watchlist
   - People looking for their next movie to watch
   - Users who want AI-powered movie recommendations
   - Anyone who loves cinema and wants a smart movie companion

   WHY CHOOSE CINESCOPE?
   - Free to use with all features unlocked
   - No ads or intrusive pop-ups
   - Fast and responsive performance
   - Regular updates with new features
   - Powered by TMDb's extensive movie database

   Download CineScope today and never miss out on great movies again!

   ---
   Note: CineScope uses TMDb API for movie data. This product uses the TMDb API but is not endorsed or certified by TMDb.
   ```

   **App icon**: Upload your 512x512 icon (from `app/src/main/res/drawable/cinescope_icon.png`)

   **Feature graphic**: Create a 1024x500 banner image showcasing your app

   **Screenshots**: Take at least 2-8 screenshots from your app (recommended: home, movie details, watchlist, profile, AI chat)

### 4.3 Privacy Policy

1. In **Store presence** → **Store settings**

2. Find **Privacy Policy** section

3. Enter your privacy policy URL:
   ```
   https://suraj0834.github.io/CineScope_FE/privacy-policy.html
   ```

4. Click **Save**

### 4.4 App Content

1. Go to **Policy** → **App content**

2. Complete all the questionnaires:

   **Privacy Policy**: Already added above

   **Ads**: Select "No, my app does not contain ads"

   **Content ratings**:
   - Click **Start questionnaire**
   - Select app category: **Utility, Productivity, Communication, or Other**
   - Answer questions honestly (no violence, no user-generated content, etc.)
   - Submit for rating

   **Target audience**:
   - Select "13+" as minimum age
   - Declare that your app is not designed for children

   **News apps**: Not applicable

   **COVID-19 contact tracing and status apps**: Not applicable

   **Data safety**:
   - Click **Start**
   - Answer what data you collect:
     * Personal info: Name, Email
     * App activity: App interactions, In-app search history
   - All data is encrypted in transit
   - Users can request deletion
   - Data is collected for functionality, analytics

   **Government apps**: Not applicable

### 4.5 Release Your App

1. Go to **Release** → **Production**

2. Click **Create new release**

3. Upload your signed APK or AAB file

4. Add release notes:
   ```
   🎉 Initial Release - Version 1.0

   Features:
   - Browse trending and popular movies
   - Search movies by title
   - Create personal watchlist and favorites
   - AI-powered chat for movie recommendations
   - Multi-language support (12+ languages)
   - Dark mode support
   - Secure user authentication

   Thank you for using CineScope!
   ```

5. Review and rollout to production

## Alternative Privacy Policy URLs

If GitHub Pages doesn't work for some reason, you can use these alternatives:

### Option 1: Netlify (Free)
1. Go to [Netlify](https://netlify.com)
2. Drag and drop your `docs` folder
3. Get instant URL

### Option 2: Firebase Hosting (Free)
1. Install Firebase CLI: `npm install -g firebase-tools`
2. Run `firebase init hosting`
3. Deploy: `firebase deploy --only hosting`

### Option 3: Google Sites (Free)
1. Go to [Google Sites](https://sites.google.com)
2. Create a new site
3. Copy-paste your privacy policy content
4. Publish and get URL

## Troubleshooting

### GitHub Pages not working?
- Make sure the branch is set to `main` and folder is `/docs`
- Wait 5-10 minutes after enabling
- Check if repository is public (Settings → General)

### Privacy Policy URL rejected by Google Play?
- Make sure the URL is publicly accessible (not behind login)
- URL must use HTTPS (GitHub Pages uses HTTPS by default)
- Privacy policy must be in HTML format (not PDF)
- Make sure there are no typos in the URL

## Your Privacy Policy URL

Once GitHub Pages is enabled, your privacy policy will be accessible at:

```
https://suraj0834.github.io/CineScope_FE/privacy-policy.html
```

Copy this URL and paste it in Google Play Console when prompted for privacy policy.

## Important Notes

1. **Keep it updated**: Update the privacy policy whenever you add new features that collect data

2. **Version control**: The "Last Updated" date is important - update it when you make changes

3. **Contact email**: Make sure `support@cinescope.app` is a valid email or update it to your actual email

4. **Review before publishing**: Read the entire privacy policy to ensure it accurately reflects your app

## Need Help?

If you face any issues:
1. Check GitHub repository settings
2. Verify the files are in the `docs` folder
3. Make sure repository is public
4. Contact GitHub support if pages don't enable

---

**Good luck with your Google Play Store submission! 🚀**
