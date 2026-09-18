package ovh.gabrielhuav.flasklogin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ovh.gabrielhuav.flasklogin.data.api.RetrofitClient
import ovh.gabrielhuav.flasklogin.data.api.TaskDto

@Composable
fun CrudScreen() {
    var tasks by remember { mutableStateOf<List<TaskDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Variables para creación/edición
    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskDto?>(null) }
    var inputTitle by remember { mutableStateOf("") }
    var inputDescription by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Cargar la lista de tareas desde la API
    fun loadTasks() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.getTasks()
                if (response.isSuccessful) {
                    tasks = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error al obtener las tareas"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión con la API"
            } finally {
                isLoading = false
            }
        }
    }

    // Cargar al entrar a la pantalla
    LaunchedEffect(Unit) {
        loadTasks()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTask = null
                inputTitle = ""
                inputDescription = ""
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Tarea")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { loadTasks() }) { Text("Reintentar") }
                    }
                }
                tasks.isEmpty() -> {
                    Text(
                        text = "No hay tareas registradas.",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(tasks) { task ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                                        if (task.description.isNotBlank()) {
                                            Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                    Row {
                                        IconButton(onClick = {
                                            editingTask = task
                                            inputTitle = task.title
                                            inputDescription = task.description
                                            showDialog = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = {
                                            task.id?.let { id ->
                                                scope.launch {
                                                    RetrofitClient.instance.deleteTask(id)
                                                    loadTasks()
                                                }
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo para Agregar / Editar
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (editingTask == null) "Nueva Tarea" else "Editar Tarea") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = inputTitle,
                            onValueChange = { inputTitle = it },
                            label = { Text("Título") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = inputDescription,
                            onValueChange = { inputDescription = it },
                            label = { Text("Descripción") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            val payload = TaskDto(
                                id = editingTask?.id,
                                title = inputTitle,
                                description = inputDescription
                            )
                            if (editingTask == null) {
                                RetrofitClient.instance.createTask(payload)
                            } else {
                                editingTask?.id?.let { id ->
                                    RetrofitClient.instance.updateTask(id, payload)
                                }
                            }
                            showDialog = false
                            loadTasks()
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}