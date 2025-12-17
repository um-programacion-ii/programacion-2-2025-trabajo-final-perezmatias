package ar.edu.um.movil

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Ya no necesitamos estado de 'token' porque está fijo en NetworkClient
        var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }

        // Navegación Simplificada: Lista -> Detalle
        if (eventoSeleccionado == null) {
            EventosScreen(
                onEventoClick = { evento -> eventoSeleccionado = evento }
            )
        } else {
            AsientosScreen(
                evento = eventoSeleccionado!!,
                onBack = { eventoSeleccionado = null }
            )
        }
    }
}

// Nota: Ya no existe LoginScreen

@Composable
fun EventosScreen(onEventoClick: (Evento) -> Unit) {
    var eventos by remember { mutableStateOf<List<Evento>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val client = remember { NetworkClient() }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                // CORRECCIÓN: Llamamos a getEventos() sin parámetros
                // (El token se inyecta solo dentro del cliente)
                eventos = client.getEventos()
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
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { onEventoClick(evento) },
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(evento.titulo ?: "Sin título", style = MaterialTheme.typography.h6)
                        Text(evento.descripcion ?: "", style = MaterialTheme.typography.body2)
                        Spacer(Modifier.height(8.dp))
                        Text("Precio: $${evento.precio}", color = MaterialTheme.colors.primary)
                        Text("Sala: ${evento.cantidadFilas ?: 0}x${evento.cantidadColumnas ?: 0} asientos", style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}