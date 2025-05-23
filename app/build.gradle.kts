plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.fittrackapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fittrackapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // firestore
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit with Scalar Converter
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.google.code.gson:gson:2.10.1")

    // OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("io.coil-kt:coil-compose:2.6.0")


    val work_version = "2.10.1"

    // (Java only)
    implementation("androidx.work:work-runtime:$work_version")

    // Kotlin + coroutines
    implementation("androidx.work:work-runtime-ktx:$work_version")

    // optional - RxJava2 support
    implementation("androidx.work:work-rxjava2:$work_version")

    // optional - GCMNetworkManager support
    implementation("androidx.work:work-gcm:$work_version")

    // optional - Test helpers
    androidTestImplementation("androidx.work:work-testing:$work_version")

    // optional - Multiprocess support
    implementation("androidx.work:work-multiprocess:$work_version")

    ksp(libs.androidx.room.compiler)


    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation("com.mapbox.navigationcore:android:3.8.6")  // Adds core Navigation SDK functionality

    // Search by category

//    implementation("com.mapbox.search:autofill:2.12.0-beta.1")
    implementation("com.mapbox.search:discover:2.12.0")
//    implementation("com.mapbox.search:place-autocomplete:2.12.0-beta.1")
//    implementation("com.mapbox.search:offline:2.12.0-beta.1")
    implementation("com.mapbox.search:mapbox-search-android:2.12.0")
//    implementation("com.mapbox.search:mapbox-search-android-ui:2.12.0-beta.1")

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    // TODO: Add the dependencies for Firebase Authentication library
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Also add the dependencies for the Credential Manager libraries and specify their versions
    implementation("androidx.credentials:credentials:1.3.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation("com.mapbox.extension:maps-compose:11.11.0")
    implementation("com.mapbox.maps:android:11.11.0")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material)
    implementation(platform("androidx.compose:compose-bom:33.13.0"))
    implementation("androidx.compose.material3:material3")
    implementation ("androidx.compose.material:material-icons-extended")
    implementation(libs.beetablescompose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}