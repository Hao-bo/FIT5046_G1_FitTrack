package com.example.fittrackapp.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

/**
 * Composable function for the Exercise Detail Screen.
 * Displays detailed information about a specific exercise.
 *
 * @param navController The NavController for navigation actions.
 * @param exerciseId The ID of the exercise to display.
 * @param muscleName The name of the muscle group, used in the TopAppBar title.
 * @param viewModel The ViewModel responsible for fetching and managing exercise data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exerciseId: Int,
    muscleName: String,
    viewModel: ExerciseViewModel = viewModel(factory = ExerciseViewModelFactory())
) {
    // LaunchedEffect to fetch exercise information when exerciseId changes.
    // This ensures data is loaded or reloaded if the user navigates to this screen
    // with a different exercise ID.
    LaunchedEffect(exerciseId) {
        viewModel.getExerciseInfo(exerciseId)
    }

    // Collect the UI state from the ViewModel as a Compose State.
    // This allows the UI to reactively update when the state changes.
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "$muscleName Exercise") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Handle different UI states: Loading, Error, Success.
            when (val state = uiState) {
                is ExerciseUiState.Loading -> {
                    // Display a CircularProgressIndicator in the center when data is loading.
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ExerciseUiState.Error -> {
                    // Display an error message and a retry button if data fetching fails.
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getExerciseInfo(exerciseId) }) {
                            Text("Retry")
                        }
                    }
                }
                is ExerciseUiState.Success -> {
                    // Display the exercise information when data is successfully loaded.
                    val exercise = state.exerciseInfo
                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(scrollState)
                    ) {
                        // Get the main image URL, if it exists
                        val mainImageUrl = exercise.images.find { it.isMain }?.image

                        if (mainImageUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(mainImageUrl)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = "Exercise Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // English sport name
                        val englishTranslation = exercise.translations.find { it.language == 2 }
                        val exerciseName = englishTranslation?.name ?: "Exercise"

                        Text(
                            text = exerciseName,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Category
                        Text(
                            text = "Category: ${exercise.category.name}",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Description
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val description = englishTranslation?.description ?: "No description available"
                        val cleanDescription = description
                            .replace("<p>", "")
                            .replace("</p>", "\n\n")
                            .trim()
                        Text(
                            text = cleanDescription,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Muscles
                        Text(
                            text = "Primary Muscles",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        exercise.muscles.forEach { muscle ->
                            Text(
                                text = "• ${muscle.nameEn}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Secondary Muscles
                        if (exercise.musclesSecondary.isNotEmpty()) {
                            Text(
                                text = "Secondary Muscles",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            exercise.musclesSecondary.forEach { muscle ->
                                Text(
                                    text = "• ${muscle.nameEn}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Equipment
                        if (exercise.equipment.isNotEmpty()) {
                            Text(
                                text = "Equipment",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            exercise.equipment.forEach { equipment ->
                                Text(
                                    text = "• ${equipment.name}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Notes
                        val notes = englishTranslation?.notes
                        if (notes != null && notes.isNotEmpty()) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            notes.forEach { note ->
                                Text(
                                    text = "• ${note.comment}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}