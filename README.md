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
  <img src="https://github.com/dannestulla/CifraFinder/assets/62083486/c9e7eee8-0505-45fc-bbcc-bd8fa7cfb65d" width="300" />
  <img src="https://github.com/dannestulla/CifraFinder/assets/62083486/f8664917-0033-4afc-a7d9-3c0613696710" width="300" />
  <img src="https://github.com/dannestulla/CifraFinder/assets/62083486/04b45da5-33f4-49fe-966d-fa3ad8490ebb" width="300" />
</p>


