# FitTrackApp

## Overview

FitTrackApp is an Android application designed to help users track their fitness activities, log workouts, discover exercises, find nearby gyms, and manage their personal fitness profiles.

## Features

* **User Authentication**:
    * Sign in and Sign up using Email & Password.
    * Google Sign-In for quick access.
* **Workout Management**:
    * Log new workout sessions with details like date, time, type, duration, and notes.
    * View past workout records, filterable by specific dates or entire months.
* **Exercise Discovery**:
    * Browse and view detailed information about various exercises.
* **Gym Locator & Navigation**:
    * Interactive map to search for and locate nearby gyms.
    * Generate routes and navigate to selected gyms.
* **Profile Management**:
    * View and edit personal fitness details such as height, weight, fitness goals, and weekly workout targets.
* **Workout Reminders**:
    * Scheduled notifications to remind users to log their daily workouts.

## Project Structure

The project aims to follow a clean architecture pattern, with code organized into layers and feature modules:

* **`data`**: Contains data sources (local Room database, remote APIs), models, and repositories.
    * `local`: Room database entities (`Workout.kt`), DAO (`WorkoutDao.kt`), and database class (`WorkoutDatabase.kt`).
    * `remote`: API client definitions (e.g., `ApiClient.kt`, `WgerApi.kt` for exercise data).
    * `model`: Data classes like `ExerciseInfo.kt`, `User.kt`, `WorkoutSession.kt`.
    * `repository`: Repositories like `WorkoutRepository.kt`, `UserRepository.kt`, `ExerciseRepository.kt`.
* **`di`**: Dependency injection setup (e.g., `Graph.kt`).
* **`ui`**: Contains Composable screens and their respective ViewModels, organized by feature:
    * `auth`: `LoginScreen.kt`, `AuthViewModel.kt`
    * `workout`: `AddWorkoutScreen.kt`, `WorkoutViewModel.kt`, `ExerciseDetailScreen.kt`, `ExerciseViewModel.kt`
    * `profile`: `ProfileScreen.kt`, `EditProfileScreen.kt`
    * `home`: `HomeScreen.kt`
    * `form`: `FormScreen.kt`, `FormViewModel.kt`
    * `map`: `MapScreen.kt`, `MapViewModel.kt`
    * `theme`: App theme definitions.
* Root Package: `FitTrackApplication.kt`, `MainActivity.kt`, `ReminderWorker.kt`.

## Setup and Configuration

### 1. Clone the Repository
The default development branch is `develop`.
```bash
git clone https://github.com/Hao-bo/FIT5046_G1_FitTrack
git checkout develop

2. Firebase Setup
This project uses Firebase for authentication.

Go to the Firebase Console and create a new project or use an existing one.
Add an Android app to your Firebase project with the package name com.example.fittrackapp (or your actual package name).
Download the google-services.json file provided by Firebase and place it in the app/ directory of your Android project.
Important for Google Sign-In:
You MUST add the SHA-1 fingerprint of your signing certificate(s) (both debug and release) to your Firebase project settings.
To get your debug SHA-1 key, you can typically use the command:
Bash

./gradlew signingReport
Or find it via Android Studio: Gradle tab -> app -> Tasks -> android -> signingReport.
Add this SHA-1 key in the Firebase console: Project settings -> Your apps -> Android app -> Add fingerprint.
3. Mapbox Setup
The application uses Mapbox for map functionalities.

You will likely need a Mapbox public access token.
This token is usually configured in the AndroidManifest.xml file or programmatically within the application. Example:

<meta-data android:name="MAPBOX_ACCESS_TOKEN"
           android:value="YOUR_MAPBOX_ACCESS_TOKEN" />
Replace YOUR_MAPBOX_ACCESS_TOKEN with your actual token.
4. Wger API (for Exercise Data)
The application might use the Wger Workout Manager API for fetching exercise information (inferred from WgerApi.kt and ExerciseRepository.kt).

If this API requires an API key or specific configuration, ensure it is correctly set up within the ApiClient.kt or related network configuration files.
How to Build and Run
Ensure all setup steps above (especially Firebase and any necessary API keys) are completed.
Open the project in Android Studio.
Let Gradle sync and download all dependencies.
Select a target device or emulator.
Run the application (usually Shift+F10 or the "Run" button).
