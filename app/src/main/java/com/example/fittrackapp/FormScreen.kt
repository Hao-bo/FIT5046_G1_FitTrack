package com.example.fittrackapp

import android.app.Application
import android.icu.util.Calendar
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.breens.beetablescompose.BeeTablesCompose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel


@Entity(tableName = "workout_data")
data class WorkoutData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val time: String,
    val category: String
)

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_data")
    suspend fun getAll(): List<WorkoutData>

    @Query("SELECT * FROM workout_data WHERE date = :selectedDate")
    suspend fun getByDate(selectedDate: String): List<WorkoutData>

    @Query("SELECT * FROM workout_data WHERE strftime('%m', date) = :month")
    suspend fun getByMonth(month: String): List<WorkoutData>
}

@Database(entities = [WorkoutData::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fittrack_database"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).workoutDao()

    private val _dayList = MutableStateFlow<List<WorkoutData>>(emptyList())
    val dayList: StateFlow<List<WorkoutData>> = _dayList

    private val _monthList = MutableStateFlow<List<WorkoutData>>(emptyList())
    val monthList: StateFlow<List<WorkoutData>> = _monthList

    fun loadByDate(date: String) {
        viewModelScope.launch {
            _dayList.value = dao.getByDate(date)
        }
    }

    fun loadByMonth(month: String) {
        viewModelScope.launch {
            _monthList.value = dao.getByMonth(month.padStart(2, '0'))
        }
    }

    fun loadAll() {
        viewModelScope.launch {
            val all = dao.getAll()
            _dayList.value = all
            _monthList.value = all
        }
    }
}

class WorkoutViewModelFactory(private val application: Application) : ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WorkoutViewModel(application) as T
    }
}

@ExperimentalMaterial3Api
@Composable
fun FormScreen(viewModel: WorkoutViewModel = viewModel(factory = WorkoutViewModelFactory(LocalContext.current.applicationContext as Application))) {
    val dayData by viewModel.dayList.collectAsState()
    val monthData by viewModel.monthList.collectAsState()

    var selectedDate by remember { mutableStateOf("") }
    var selectedMonth by remember { mutableStateOf("01") }

    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)) {

            DateFilter(selectedDate = selectedDate) {
                selectedDate = it
                viewModel.loadByDate(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            BeeTablesCompose(
                data = dayData.map { WorkoutData(0, "", it.time, it.category) },
                headerTableTitles = listOf("Time", "Category")
            )

            Spacer(modifier = Modifier.height(32.dp))

            MonthFilter(selectedMonth = selectedMonth) {
                selectedMonth = it
                viewModel.loadByMonth(it)
            }

            Spacer(modifier = Modifier.height(16.dp))

            BeeTablesCompose(
                data = monthData.map { WorkoutData(0, it.date, "", it.category) },
                headerTableTitles = listOf("Date", "Category")
            )
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun DateFilter(selectedDate: String, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    val displayDate = if (selectedDate.isNotEmpty()) selectedDate else "Search by date"

    TextField(
        value = displayDate,
        onValueChange = {},
        readOnly = true,
        label = { Text("Search by date") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        trailingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.calendar_icon),
                contentDescription = "Select Date",
                modifier = Modifier
                    .clickable { showDatePicker = true }
                    .size(40.dp)
            )
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) onDateSelected(formatter.format(Date(selected)))
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthFilter(selectedMonth: String, onMonthSelected: (String) -> Unit) {
    val months = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    var isExpanded by remember { mutableStateOf(false) }
    var current by remember { mutableStateOf(selectedMonth) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = current,
            onValueChange = {},
            label = { Text("Month") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        current = month
                        isExpanded = false
                        onMonthSelected(month)
                    }
                )
            }
        }
    }
}
