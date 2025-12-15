package ar.edu.um.movil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var token by remember { mutableStateOf("") }

        if (token.isEmpty()) {
            LoginScreen(onLoginSuccess = { token = it })
        } else {
            EventosScreen(token = token)
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var user by remember { mutableStateOf("admin") }
    var pass by remember { mutableStateOf("admin") }
    var status by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val client = remember { NetworkClient() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Trabajo Final 2025", style = MaterialTheme.typography.h5)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(value = user, onValueChange = { user = it }, label = { Text("Usuario") })
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            scope.launch {
                status = "Conectando..."
                try {
                    val t = client.login(user, pass)
                    status = "¡Éxito!"
                    onLoginSuccess(t)
                } catch (e: Exception) {
                    status = "Error: ${e.message}"
                    e.printStackTrace()
                }
            }
        }) {
            Text("Ingresar")
        }
        Text(status, color = MaterialTheme.colors.error)
    }
}

@Composable
fun EventosScreen(token: String) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val client = remember { NetworkClient() }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                eventos = client.getEventos(token)
            } catch (e: Exception) {
                println("Error cargando eventos: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cartelera") }) }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(eventos) { evento ->
                Card(modifier = Modifier.padding(8.dp).fillMaxWidth(), elevation = 4.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(evento.titulo ?: "Sin título", style = MaterialTheme.typography.h6)
                        Text(evento.descripcion ?: "", style = MaterialTheme.typography.body2)
                        Text("Precio: $${evento.precio}", color = MaterialTheme.colors.primary)
                    }
                }
            }
        }
    }
}