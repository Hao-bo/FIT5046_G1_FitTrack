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
