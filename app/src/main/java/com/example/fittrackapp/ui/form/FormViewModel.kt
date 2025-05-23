package com.example.fittrackapp.ui.form

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.di.Graph
import com.example.fittrackapp.data.model.WorkoutSession
import com.example.fittrackapp.data.repository.WorkoutRepository
import com.example.fittrackapp.ui.auth.AuthViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

/**
 * ViewModel for the FormScreen.
 * It handles the business logic for fetching and managing workout session data
 * based on selected dates or months, and observes the current user's ID from AuthViewModel.
 *
 * @param workoutRepository The repository for accessing workout data. Defaults to Graph.workoutRepository.
 * @param authViewModel The ViewModel responsible for authentication state, providing the current user ID.
 */
class FormViewModel(
    private val workoutRepository: WorkoutRepository = Graph.workoutRepository,
    authViewModel: AuthViewModel,
) : ViewModel() {

    // Observe current user ID
    private val currentUserId = authViewModel.currentUserId

    // the time by user selected
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    private val _selectedYearMonth = MutableStateFlow<Pair<Int, Int>?>(null)

    /**
     * StateFlow that emits a list of [com.example.fittrackapp.data.model.WorkoutSession] for the currently selected date.
     * It observes changes in both `currentUserId` and `_selectedDateMillis`.
     * If either is null, it emits an empty list. Otherwise, it queries the repository.
     * The Chinese comment "when _selectedDateMillis update，automatically query the exercise data of the corresponding date"
     * translates to "When _selectedDateMillis updates, automatically query the exercise data of the corresponding date".
     */
    // when _selectedDateMillis update，automatically query the exercise data of the corresponding date
    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutsForSelectedDate: StateFlow<List<WorkoutSession>> =
        authViewModel.currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                _selectedDateMillis.flatMapLatest { millis ->
                    if (millis == null) {
                        flowOf(emptyList()) // return emptyList if doesn't select _selectedDateMillis
                    } else {
                        val (dayStart, dayEnd) = calculateDayTimestamps(millis)
                        Log.d(ContentValues.TAG, "workoutsForSelectedDate - querying database for userId: $userId, dayStart: $dayStart, dayEnd: $dayEnd")
                        workoutRepository.getWorkoutsByDate(userId, dayStart, dayEnd)
                            .onEach { workoutList ->
                                Log.d(ContentValues.TAG, "workoutsForSelectedDate - workoutList: $workoutList")
                                workoutList.forEach { workout ->
                                    Log.d(ContentValues.TAG, "workoutsForSelectedDate - workout: id = $workout.id, activityType = ${workout.activityType}")
                                }
                            }
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly, // 改为 Eagerly 确保立即开始
            initialValue = emptyList()
//            viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
        )


    /**
     * Clears the selected date and month, effectively resetting the filters.
     * This will cause `workoutsForSelectedDate` and `workoutsForSelectedMonth` to emit empty lists
     * if they rely on these null values.
     */
    fun clearSelectedDate() {
        _selectedDateMillis.value = null
        _selectedYearMonth.value = null
    }


    /**
     * StateFlow that emits a list of [WorkoutSession] for the currently selected year and month.
     * It observes changes in both `currentUserId` and `_selectedYearMonth`.
     * If either is null, it emits an empty list. Otherwise, it queries the repository.
     */
    val workoutsForSelectedMonth: StateFlow<List<WorkoutSession>> =
        authViewModel.currentUserId.flatMapLatest { userId ->
            if (userId == null) {
                flowOf(emptyList())
            } else {
                _selectedYearMonth.flatMapLatest { yearMonthPair ->
                    if (yearMonthPair == null) {
                        flowOf(emptyList())
                    } else {
                        val (year, month) = yearMonthPair
                        val (monthStart, monthEnd) = calculateMonthTimestamps(year, month)
                        workoutRepository.getWorkoutsByMonth(userId,monthStart, monthEnd)
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.Eagerly, // 改为 Eagerly 确保立即开始
            initialValue = emptyList()
//            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )



    /**
     * Updates the date selected by the user and triggers a data load.
     * @param dateMillis the timestamp of the selected date
     */
    fun loadWorkoutsForDate(dateMillis: Long) {
        _selectedDateMillis.value = dateMillis
    }

    /**
     * Updates the year and month selected by the user and triggers a data load.
     * @param year
     * @param month
     */
    fun loadWorkoutsForMonth(year: Int, month: Int) {
        if (month < 1 || month > 12) {
            return
        }
        _selectedYearMonth.value = Pair(year, month)
    }


    /**
     * Calculates the start and end timestamps of the date for a given timestamp
     * start time：00:00:00.000
     * end time：23:59:59.999
     */
    private fun calculateDayTimestamps(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance() // use default timezone
        calendar.timeInMillis = timestamp

        // Set to start of day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayStart = calendar.timeInMillis

        // Set to end of day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val dayEnd = calendar.timeInMillis

        return Pair(dayStart, dayEnd)
    }

    /**
     * Calculates the start and end timestamps for a given year and month.
     * The start time is 00:00:00.000 of the first day of the month.
     * The end time is 23:59:59.999 of the last day of the month.
     * Uses the default device timezone.
     *
     * @param year The year.
     * @param month The month (1-12).
     * @return A Pair of Longs: (startTimestamp, endTimestamp).
     */
    private fun calculateMonthTimestamps(year: Int, month: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance() // use default timezone
        calendar.clear()

        // set the start of month 00:00:00.000
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1) // Calendar month start from 0
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        // set the end of the month 23:59:59.999
        // calculate the first day of next month then minus 1 millisecond
        // that would be the last time of current month
        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.MILLISECOND, -1)
        val monthEnd = calendar.timeInMillis

        return Pair(monthStart, monthEnd)
    }

    /**
     * Initialization block.
     * Loads workout records for the current day and current month by default when the ViewModel is created.
     * The Chinese comment "Load the current day's and month's exercise records by default" is accurate.
     */
    init {
        // Load the current day's and month's exercise records by default
        loadWorkoutsForDate(System.currentTimeMillis())
        val cal = Calendar.getInstance()
        loadWorkoutsForMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

}