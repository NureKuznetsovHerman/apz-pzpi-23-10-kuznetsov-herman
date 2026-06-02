package com.example.energyview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.energyview.models.AvailableSensors
import com.example.energyview.models.Device
import com.example.energyview.models.DeviceUiState
import com.example.energyview.models.Sensor
import com.example.energyview.ui.theme.EnergyViewTheme
import com.example.energyview.viewmodel.AdminViewModel
import com.example.energyview.viewmodel.DeviceViewModel

class MainActivity : ComponentActivity() {

    private val deviceViewModel: DeviceViewModel by viewModels()
    private val adminViewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EnergyViewTheme {
                var currentUserRole by remember { mutableStateOf("Admin") }

                MainAppScreen(
                    deviceViewModel = deviceViewModel,
                    adminViewModel = adminViewModel,
                    userRole = currentUserRole,
                    onRoleChange = { currentUserRole = it }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    deviceViewModel: DeviceViewModel,
    adminViewModel: AdminViewModel,
    userRole: String,
    onRoleChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val isAdmin = userRole == "Admin"
    var currentRoute by remember { mutableStateOf("devices") }

    LaunchedEffect(Unit) {
        adminViewModel.loadAdminData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Моніторинг Енергії ($userRole)") },
                actions = {
                    TextButton(onClick = { onRoleChange(if (isAdmin) "Guest" else "Admin") }) {
                        Text(if (isAdmin) "Режим: Гість" else "Режим: Адмін", color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Пристрої") },
                    selected = currentRoute == "devices",
                    onClick = { currentRoute = "devices"; navController.navigate("devices") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Юзери") },
                    selected = currentRoute == "users",
                    onClick = { currentRoute = "users"; navController.navigate("users") }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                    label = { Text("Межі") },
                    selected = currentRoute == "thresholds",
                    onClick = { currentRoute = "thresholds"; navController.navigate("thresholds") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "devices",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("devices") {
                DevicesRouteScreen(viewModel = deviceViewModel, isAdmin = isAdmin)
            }
            composable("users") {
                UsersScreen(viewModel = adminViewModel, isAdmin = isAdmin)
            }
            composable("thresholds") {
                ThresholdsScreen(viewModel = adminViewModel, isAdmin = isAdmin)
            }
        }
    }
}

// ==================== ЕКРАН КОРИСТУВАЧІВ ====================
@Composable
fun UsersScreen(viewModel: AdminViewModel, isAdmin: Boolean) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Додати користувача")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                viewModel.errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }

                if (viewModel.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(viewModel.users) { user ->
                            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(user.fullName, style = MaterialTheme.typography.titleMedium)
                                        Text("@${user.username} • Роль: ${user.role}", style = MaterialTheme.typography.bodyMedium)
                                    }
                                    if (isAdmin) {
                                        IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showAddDialog) {
                var name by remember { mutableStateOf("") }
                var username by remember { mutableStateOf("") }
                var role by remember { mutableStateOf("User") }
                var password by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text("Новий користувач") },
                    text = {
                        Column {
                            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Повне ім'я") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Нікнейм") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Роль (Admin/User)") }, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.addUser(username, name, role, password)
                            showAddDialog = false
                        }) { Text("Створити") }
                    },
                    dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Скасувати") } }
                )
            }
        }
    }
}

// ==================== ЕКРАН МЕЖ (ТІЛЬКИ КОНФІГУРАЦІЯ) ====================
@Composable
fun ThresholdsScreen(viewModel: AdminViewModel, isAdmin: Boolean) {
    var showAddThresholdDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { showAddThresholdDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Додати межу")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                item {
                    Text("Конфігурація меж датчиків (Thresholds)", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
                }

                if (viewModel.thresholds.isEmpty()) {
                    item {
                        Text("Межі ще не налаштовані. Натисніть кнопку + нижче, щоб додати.", modifier = Modifier.padding(vertical = 16.dp), color = Color.Gray)
                    }
                }

                items(viewModel.thresholds) { t ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Датчик ID: ${t.sensorId}", style = MaterialTheme.typography.titleMedium)
                                Text("Допустимі значення: від ${t.minValue} до ${t.maxValue}", style = MaterialTheme.typography.bodyMedium)
                                t.alertMessage?.let { Text("Попередження: $it", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                            }
                            if (isAdmin) {
                                IconButton(onClick = { viewModel.deleteThreshold(t.thresholdId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Видалити межу", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            if (showAddThresholdDialog) {
                var sId by remember { mutableStateOf("") }
                var minV by remember { mutableStateOf("") }
                var maxV by remember { mutableStateOf("") }
                var msg by remember { mutableStateOf("") }

                AlertDialog(
                    onDismissRequest = { showAddThresholdDialog = false },
                    title = { Text("Додати межу безпеки") },
                    text = {
                        Column {
                            OutlinedTextField(value = sId, onValueChange = { sId = it }, label = { Text("ID Датчика") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = minV, onValueChange = { minV = it }, label = { Text("Мінімальна межа") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = maxV, onValueChange = { maxV = it }, label = { Text("Максимальна межа") }, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Повідомлення при виході за межі") }, modifier = Modifier.fillMaxWidth())
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val sensorIdInt = sId.toIntOrNull() ?: 0
                            val minDouble = minV.toDoubleOrNull() ?: 0.0
                            val maxDouble = maxV.toDoubleOrNull() ?: 0.0
                            viewModel.addThreshold(sensorIdInt, minDouble, maxDouble, msg)
                            showAddThresholdDialog = false
                        }) { Text("Зберегти") }
                    },
                    dismissButton = { TextButton(onClick = { showAddThresholdDialog = false }) { Text("Скасувати") } }
                )
            }
        }
    }
}

// ==================== ЕКРАН ПРИСТРОЇВ (DEVICES) ====================
@Composable
fun DevicesRouteScreen(viewModel: DeviceViewModel, isAdmin: Boolean) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<Device?>(null) }
    var showReadingDialog by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { selectedDevice = null; showDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Додати пристрій")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = viewModel.uiState) {
                is DeviceUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is DeviceUiState.Error -> Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Button(onClick = { viewModel.fetchDevices() }) { Text("Оновити") }
                }
                is DeviceUiState.Success -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                        items(state.devices) { device ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable(enabled = isAdmin) {
                                        selectedDevice = device
                                        showDialog = true
                                    }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = device.name, style = MaterialTheme.typography.titleLarge)
                                            Text(text = "Тип: ${device.type} | Потужність: ${device.maxPowerOutput ?: 0.0} кВт")
                                        }
                                        if (isAdmin) {
                                            IconButton(onClick = { viewModel.deleteDevice(device.deviceId) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Видалити", tint = Color.Red)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Прив'язані датчики:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                                    if (device.sensors.isNullOrEmpty()) {
                                        Text("Немає підключених датчиків", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    } else {
                                        device.sensors.forEach { sensor ->
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "• ${sensor.sensorType} (${sensor.unit}) [ID: ${sensor.sensorId}]",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = "+ Показник",
                                                    modifier = Modifier
                                                        .background(MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small)
                                                        .clickable { showReadingDialog = sensor.sensorId }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            showReadingDialog?.let { sensorId ->
                var readingValue by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showReadingDialog = null },
                    title = { Text("Внести вимірювання (Датчик ID: $sensorId)") },
                    text = { OutlinedTextField(value = readingValue, onValueChange = { readingValue = it }, label = { Text("Значення виміру") }, modifier = Modifier.fillMaxWidth()) },
                    confirmButton = {
                        Button(onClick = {
                            readingValue.toDoubleOrNull()?.let { viewModel.addSensorReading(sensorId, it) }
                            showReadingDialog = null
                        }) { Text("Надіслати") }
                    },
                    dismissButton = { TextButton(onClick = { showReadingDialog = null }) { Text("Скасувати") } }
                )
            }

            if (showDialog && isAdmin) {
                DeviceDialog(
                    device = selectedDevice,
                    onDismiss = { showDialog = false },
                    onSave = { name, type, power, isActive, selectedSensorType, selectedSensorUnit ->

                        val initialSensor = Sensor(
                            sensorId = 0, deviceId = 0,
                            sensorType = selectedSensorType,
                            unit = selectedSensorUnit
                        )

                        val targetDevice = Device(
                            deviceId = selectedDevice?.deviceId ?: 0,
                            name = name, type = type, isActive = isActive,
                            maxPowerOutput = power.toDoubleOrNull(),
                            sensors = if (selectedDevice == null) listOf(initialSensor) else selectedDevice!!.sensors
                        )
                        if (selectedDevice == null) viewModel.addDevice(targetDevice) else viewModel.updateDevice(selectedDevice!!.deviceId, targetDevice)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun DeviceDialog(
    device: Device?,
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, power: String, isActive: Boolean, sensorType: String, sensorUnit: String) -> Unit
) {
    var name by remember { mutableStateOf(device?.name ?: "") }
    var type by remember { mutableStateOf(device?.type ?: "") }
    var power by remember { mutableStateOf(device?.maxPowerOutput?.toString() ?: "") }
    var isActive by remember { mutableStateOf(device?.isActive ?: true) }

    var selectedSensorIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (device == null) "Створення пристрою" else "Редагування") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Назва пристрою") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Тип (напр. Solar, Wind)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = power, onValueChange = { power = it }, label = { Text("Потужність (кВт)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                if (device == null) {
                    Text("Вкажіть тип сенсора для девайса:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = 4.dp))
                    AvailableSensors.types.forEachIndexed { index, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSensorIndex = index }
                                .padding(vertical = 4.dp)
                                .background(if (selectedSensorIndex == index) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        ) {
                            RadioButton(selected = selectedSensorIndex == index, onClick = { selectedSensorIndex = index })
                            Text(text = pair.first, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isActive, onCheckedChange = { isActive = it })
                    Text("Пристрій активний")
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val sensorPair = AvailableSensors.types[selectedSensorIndex]
                val cleanType = sensorPair.first.substringAfter("(").substringBefore(")")
                onSave(name, type, power, isActive, cleanType, sensorPair.second)
            }) { Text("Зберегти") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Скасувати") } }
    )
}