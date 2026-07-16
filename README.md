# 🔱 Torrnado

Torrnado is a modern, material-designed Android client for searching, indexing, and organizing torrents. Built with Jetpack Compose, Kotlin, MVVM, and Hilt, it offers a seamless and highly customizable experience.

---

## ✨ Features

- **🎨 Modern Material 3 UI**: Interactive, responsive layout leveraging Compose.
- **🌈 Deep Customization**: Custom color palette accents using Material Kolor, with full support for Light, Dark, System, and **AMOLED Black** themes.
- **🔍 Active Search**: Expandable search bar with active states, focus management, and persistent search history.
- **📂 Feeds & Transfers Nav**: Navigation integration with dedicated views to manage active transfers, feeds, and indexer search results.
- **🚀 Automated Build Pipeline**: Fully-configured GitHub Actions workflow that compiles a signed or unsigned release APK on push/tags.
- **🔒 Secure Local Builds**: Automatic fallback to debug keystore signing if release keys are missing locally, ensuring frictionless development.

---

## 🏗️ Project Architecture

The app is organized following clean architecture guidelines:

```
tech.jitendhull.torrnado/
├── data/          # Room DB, Retrofit API client, indexers (Prowlarr/Jackett), repositories
├── domain/        # Models, core business logic, Use Cases
├── di/            # Dependency injection modules (Hilt)
└── ui/            # Compose screens (Search, Feeds, Transfers, Settings), navigation, themes
```

---

## 🛠️ Local Build Setup

### Prerequisites
- JDK 17
- Android SDK / Android Studio (Koala or newer recommended)

### Build Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/jitendhull/Torrnado.git
   cd Torrnado
   ```
2. Build a local debug build:
   ```bash
   ./gradlew assembleDebug
   ```

### Custom Release Signing (Optional)
To sign your release builds locally:
1. Create a `keystore.properties` file in the project root:
   ```properties
   storeFile=path/to/your/release.keystore
   storePassword=your_keystore_password
   keyAlias=your_key_alias
   keyPassword=your_key_password
   ```
2. Build the signed release:
   ```bash
   ./gradlew assembleRelease
   ```
*(Note: If `keystore.properties` is missing, the build automatically falls back to the default debug key so the compile won't fail.)*

---

## 🤖 CI/CD Release Workflow

A GitHub Actions workflow is set up under `.github/workflows/release.yml`. When you push tags (`v*`) or commit messages prefixed with `release:` / `Release:`, it:
1. Sets up the JDK environment.
2. Checks for repository secrets to decode the keystore.
3. Compiles the production release APK.
4. Uploads the final APK to the GitHub run workflow artifacts for direct download.

### Configuring Secrets
To compile signed APKs on GitHub Actions, add the following secrets in your repository settings (**Settings > Secrets and variables > Actions**):

- `RELEASE_KEYSTORE_BASE64`: The Base64 encoded string of your `.keystore` or `.jks` file.
  *(Generate it using `base64 -i your_keystore.keystore | pbcopy` or equivalent)*
- `RELEASE_KEYSTORE_PASSWORD`: The main password for your keystore.
- `RELEASE_KEY_ALIAS`: The alias for your release key.
- `RELEASE_KEY_PASSWORD`: The password for your specific key alias.