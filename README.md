# FitTrack - Android Application

## FIT5046 Assessment 3 - Android App Design Proposal

**Group ID:** Lab10 Group1
**Team Members:**
* Chengqian Chang (33405727) [cite: 1]
* Liangjing Yang (34060871) [cite: 1]
* Yuejun Luo (34249826) [cite: 1]
* Xinzhi Gong (34335196) [cite: 1]

---

## Table of Contents
1.  [Application Overview](#application-overview)
    * [1.1 Application Domain and Purpose](#11-application-domain-and-purpose)
    * [1.2 Target User Group](#12-target-user-group)
2.  [Key Features](#key-features)
3.  [Screens Overview](#screens-overview)
4.  [Advanced Features Implemented](#advanced-features-implemented)
5.  [Technology Stack & Key Libraries](#technology-stack--key-libraries)
6.  [Navigating the Source Code (Important for Assessment)](#navigating-the-source-code-important-for-assessment)
7.  [How to Build and Run](#how-to-build-and-run)
8.  [Project Limitations](#project-limitations)
9.  [Acknowledgement of Generative AI Use](#acknowledgement-of-generative-ai-use)

---

## 1. Application Overview

### 1.1 Application Domain and Purpose
FitTrack is an Android application designed for personalized fitness management and location-based community engagement within the health domain. [cite: 2] It aims to bridge the gap between home workouts and professional gym environments by combining structured API-driven fitness content with real-world gym accessibility information. [cite: 3] Unlike apps focusing solely on home workouts or gym navigation, FitTrack integrates these aspects, allowing users to sync self-recorded home workout data and seamlessly transition to nearby gym facilities while maintaining continuous progress tracking. [cite: 4, 5]

### 1.2 Target User Group
The primary users for FitTrack include:
* **University students (18-25):** Often on a budget, reliant on mobile technology, and may prefer free/low-cost home workouts but also value professional guidance and gym atmospheres. [cite: 6]
* **Young professionals (26-35):** Typically time-constrained, seeking efficient workout solutions, and may need flexible home workout options. [cite: 6, 7]
* **Fitness enthusiasts (18-45):** Generally interested in detailed progress analytics and structured workout plans. [cite: 6]

User stories for characters like Lucy (university student) and Ethan (office worker) highlight the app's value in providing guided tutorials, flexible home workout plans, gym discovery, and progress tracking. [cite: 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]

---

## 2. Key Features
FitTrack offers the following key functionalities:

* **User Authentication:** Secure login and registration via email or Google Sign-In, protecting personal data and enabling a customized user experience. [cite: 24, 35]
* **Guided Workout Tutorials:** Provides users with structured workout tutorials via an API on the Home Screen, helping them follow guided exercises. Implemented using `LazyColumn` for efficient display. [cite: 18, 24]
* **Custom Workout Data Input:** Allows users to input their own workout data on the Home Screen, which is then stored. [cite: 18, 24]
* **Historical Data Retrieval & Display:** Users can query and view their historical workout data on the Form Screen, filtered by date (using a `DatePicker`) and month (using a dropdown menu). [cite: 19, 24]
* **Nearby Gym Discovery:** The Map Screen utilizes location services (Mapbox) to display nearby gyms, making it easier for users to find and reach fitness facilities. [cite: 20, 24]

---

## 3. Screens Overview

* **Sign-In/Sign-Up Screen:** Allows users to sign in or create an account using email or Google Sign-In. User credentials (email/password) are collected and stored. [cite: 18, 24]
* **Home Screen:** Displays fitness class tutorials obtained from an API (using `LazyColumn`). Users can also input and store their completed workout data here. [cite: 18, 24]
* **Form Screen:** Displays the user's exercise history. Allows users to retrieve and view records by specific dates (using a `DatePicker`) and months. Workout data is presented in tables (using `BeeTablesCompose`). [cite: 19, 24]
* **Map Screen:** Displays the location of gyms near the user, leveraging Mapbox location services. [cite: 20, 24]

---

## 4. Advanced Features Implemented

1.  **Google Authentication & Firebase Database Integration:**
    * Uses Google Authentication for quick and secure user sign-in. [cite: 35]
    * User-specific data (workout records, history, preferences) is securely stored and retrieved using Firebase Realtime Database. [cite: 36, 37]
    * Implementation involves integrating Firebase into Android Studio, using the Google Sign-In API with Jetpack Compose for the login interface, and using unique user identifiers to manage data. [cite: 38, 39, 40, 41]

2.  **Android WorkManager for Background Tasks:**
    * Automates the background upload of locally stored workout data (from a Room database) to Firebase to ensure data backup without disrupting the user experience. [cite: 43, 44]
    * A `Worker` class handles periodic syncing, with constraints to execute only when the device is idle and connected to the internet (e.g., nightly). [cite: 45, 46, 47]

---

## 5. Technology Stack & Key Libraries

* **UI Development:** Jetpack Compose
* **Authentication:** Firebase Authentication (Google Sign-In)
* **Database:**
    * Firebase Realtime Database (cloud storage)
    * Room Persistence Library (local storage)
* **Background Tasks:** Android WorkManager
* **Maps:** Mapbox SDK
* **Key UI Components/Libraries:**
    * `LazyColumn` (for efficient list display on Home Screen)
    * `DatePicker` & `DatePickerDialog` (for date selection on Form Screen)
    * `ExposedDropdownMenuBox` (for month selection on Form Screen)
    * `BeeTablesCompose` (for displaying tabular data on Form Screen)
* **Programming Language:** Kotlin

---

## 6. Navigating the Source Code (Important for Assessment)

**Please note:** The primary implementations for the UI, including key Jetpack Compose components like `LazyColumn` and `DatePicker`, and the boilerplate code for key screens, are located in the **`test/a3-UI` branch** as of the submission deadline (April 17, 2025).

The `main` branch may not reflect these completed UI implementations.

**Key source code files for UI implementation include:**

* `app/src/main/java/com/example/fittrackapp/HomeScreen.kt`: Contains the implementation of the Home Screen, including the use of `LazyColumn` for displaying workout tutorials.
* `app/src/main/java/com/example/fittrackapp/FormScreen.kt`: Contains the implementation of the Form Screen, including the `DisplayDatePicker()` composable which uses `DatePicker` and `DatePickerDialog`, as well as `TestMenu()` for month selection and `BeeTablesCompose` for data display.
* Other relevant files for authentication, data handling, and advanced features can be found within the project structure, primarily within the `test/a3-UI` branch.

We recommend checking out the `test/a3-UI` branch for a complete view of the implemented features as described in this proposal and the grading feedback.

---

## 7. How to Build and Run

1.  Clone the repository from GitHub (ensure you are on the `test/a3-UI` or `develop` branch for the latest UI implementations).
2.  Open the project in Android Studio (latest stable version recommended).
3.  Ensure you have the necessary Android SDK versions installed as specified in the `build.gradle` files.
4.  Connect an Android device or start an Android Virtual Device (Emulator).
5.  Build and run the application from Android Studio.

**Note on Firebase Setup:**
The project is configured to use Firebase for authentication and database functionalities. Ensure your Android Studio environment can connect to Firebase, or follow standard Firebase setup procedures if you are running it in a new environment (this may involve adding your own `google-services.json` file if it's not included in the repository for security reasons).

---

## 8. Project Limitations

* **Emulator Constraints:** Features like the Map functionality might not dynamically reflect real-world movement when tested on an emulator. [cite: 21]
* **Short Development Cycle:** Some pages or UI elements might lack extensive polish (e.g., complex motion transitions or animations) due to the development timeframe. [cite: 22, 23]
* **Data Privacy:** While Firebase is used, storing user data in Firebase/Room might not meet the highest security requirements for highly sensitive personal data. [cite: 23]

---

## 9. Acknowledgement of Generative AI Use

Generative AI tools (specifically ChatGPT, April 2025 version) were utilized during the preparation of the project proposal to support:
* Refining paragraph structure and language clarity. [cite: 59]
* Assisting with debugging code fragments (e.g., Mapbox, Jetpack Compose layouts). [cite: 60]
* Prototyping UI/UX components and discussing design best practices. [cite: 61]
* Conducting reference look-ups and technical research. [cite: 62]
The AI tool was used as an assistant for technical writing, logic refinement, and enhancing implementation strategies, not for generating original research ideas or final decisions. [cite: 63]
