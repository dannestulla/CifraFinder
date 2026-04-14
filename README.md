# CifraFinder

Finds the tablature of the song being played on Spotify. A great app for playing guitar while following along with the music. Features auto-scroll synchronized with song duration.

## Features

- Detects currently playing song from Spotify
- Searches for guitar tablatures on CifraClub
- Auto-scrolls tablature synchronized with song duration
- Supports manual scroll with pause/resume

## Tech Stack

- **APIs**: Spotify API, Serper.dev (Google Search)
- **UI**: Jetpack Compose, Material 3
- **Architecture**: Clean Architecture, MVVM
- **DI**: Koin
- **Networking**: Retrofit
- **Analytics**: Firebase Analytics & Crashlytics
- **CI/CD**: Bitrise with Fastlane for store deployment

## Setup

1. Clone the repository
2. Create `local.properties` with:
   ```
   SPOTIFY_CLIENT_ID="your_spotify_client_id"
   SERP_API_KEY="your_serper_dev_api_key"
   ```
3. Add your SHA-1 fingerprint to Spotify Dashboard
4. Build and run

## Download

[Google Play Store](https://play.google.com/store/apps/details?id=br.gohan.cifrafinder)

## Screenshots

<p float="left">
  <img src="https://github.com/user-attachments/assets/044c27a2-9a25-4491-9a96-bd5c76252fa7" width="300" />
  <img src="https://github.com/user-attachments/assets/26f24a1d-f470-4f8c-8249-7c2c8c56a115" width="300" />
  <img src="https://github.com/user-attachments/assets/a55ad822-3608-4a26-90e2-ea1668251104" width="300" />
</p>


