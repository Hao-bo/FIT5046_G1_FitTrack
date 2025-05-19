package com.example.fittrackapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fittrackapp.data.WorkoutRepository
import com.example.fittrackapp.data.WorkoutSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.TimeZone

class FormViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    // the time by user selected
    private val _selectedDateMillis = MutableStateFlow<Long?>(null)
    private val _selectedYearMonth = MutableStateFlow<Pair<Int, Int>?>(null)


    // when _selectedDateMillis update，automatically query the exercise data of the corresponding date
    val workoutsForSelectedDate: StateFlow<List<WorkoutSession>> = _selectedDateMillis.flatMapLatest { millis ->
        if (millis == null) {
            flowOf(emptyList()) // return emptyList if doesn't select _selectedDateMillis
        } else {
            val (dayStart, dayEnd) = calculateDayTimestamps(millis)
            workoutRepository.getWorkoutsByDate(dayStart, dayEnd)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val workoutsForSelectedMonth: StateFlow<List<WorkoutSession>> = _selectedYearMonth.flatMapLatest { yearMonthPair ->
        if (yearMonthPair == null) {
            flowOf(emptyList())
        } else {
            val (year, month) = yearMonthPair
            val (monthStart, monthEnd) = calculateMonthTimestamps(year, month)
            workoutRepository.getWorkoutsByMonth(monthStart, monthEnd)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
     * @param year
     * @param month
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

    init {
        // Load the current day's and month's exercise records by default
        loadWorkoutsForDate(System.currentTimeMillis())
        val cal = Calendar.getInstance()
        loadWorkoutsForMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1)
    }

}