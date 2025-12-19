package ar.edu.um.movil

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class AsientoUI(val fila: Int, val columna: Int) {
    var seleccionado by mutableStateOf(false)
    var ocupado by mutableStateOf(false)
}

@Composable
fun AsientosScreen(evento: Evento, onBack: () -> Unit) {
    val client = remember { NetworkClient() }
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Generar la grilla vacía
    val asientos = remember {
        val lista = mutableListOf<AsientoUI>()
        val filas = evento.cantidadFilas ?: 10
        val cols = evento.cantidadColumnas ?: 10
        for (f in 1..filas) { for (c in 1..cols) { lista.add(AsientoUI(f, c)) } }
        mutableStateListOf(*lista.toTypedArray())
    }

    // Cargar Ocupados
    LaunchedEffect(Unit) {
        scope.launch {
            val listaOcupados = client.getOcupados(evento.id)
            listaOcupados.forEach { ocupado ->
                val match = asientos.find { it.fila == ocupado.fila && it.columna == ocupado.columna }
                match?.ocupado = true
                match?.seleccionado = false
            }
        }
    }

    if (mostrarDialogo) {
        DialogoDatosComprador(
            onDismiss = { mostrarDialogo = false },
            onConfirm = { nombre, dni ->
                mostrarDialogo = false
                scope.launch {
                    mensaje = "Procesando..."
                    try {
                        val seleccionados = asientos.filter { it.seleccionado }

                        val request = VentaRequest(
                            evento.id, seleccionados.size * (evento.precio ?: 0.0),
                            seleccionados.map { AsientoVenta(it.fila, it.columna) },
                            nombre, dni
                        )
                        val resp = client.realizarVenta(request)
                        if (resp.resultado) {
                            mensaje = "¡COMPRA EXITOSA!"
                            seleccionados.forEach {
                                it.seleccionado = false
                                it.ocupado = true
                            }
                        } else mensaje = "Error: ${resp.descripcion}"
                    } catch (e: Exception) { mensaje = "Error: ${e.message}" }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evento.titulo ?: "Selección") },
                // Botón simple de volver
                navigationIcon = { TextButton(onClick = onBack) { Text("<", color = Color.White, style = MaterialTheme.typography.h6) } }
            )
        },
        bottomBar = {
            // Calculamos el total observando la lista
            val seleccionadosCount by remember { derivedStateOf { asientos.count { it.seleccionado } } }

            if (seleccionadosCount > 0) {
                Card(elevation = 8.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Total: $${seleccionadosCount * (evento.precio ?: 0.0)}")
                        Button(onClick = { mostrarDialogo = true }, Modifier.fillMaxWidth()) { Text("CONFIRMAR") }
                    }
                }
            }
        }
    ) { p ->
        Column(Modifier.padding(p).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (mensaje.isNotEmpty()) Text(mensaje, color = if(mensaje.contains("EXITOSA")) Color.Green else Color.Red, modifier = Modifier.padding(8.dp))

            Text("ESCENARIO", style = MaterialTheme.typography.caption)
            Box(Modifier.fillMaxWidth().height(4.dp).background(Color.Gray))
            Spacer(Modifier.height(10.dp))

            Box(Modifier.weight(1f).horizontalScroll(rememberScrollState()).verticalScroll(rememberScrollState())) {
                Column {
                    val filas = evento.cantidadFilas ?: 10
                    val cols = evento.cantidadColumnas ?: 10
                    for (f in 1..filas) {
                        Row {
                            for (c in 1..cols) {
                                val asiento = asientos.find { it.fila == f && it.columna == c }
                                if (asiento != null) {
                                    // Pasamos el objeto asiento directamente
                                    AsientoItem(asiento) {
                                        asiento.seleccionado = !asiento.seleccionado
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AsientoItem(asiento: AsientoUI, onClick: () -> Unit) {
    val color = when {
        asiento.ocupado -> Color.Red
        asiento.seleccionado -> Color(0xFF4CAF50) // Verde
        else -> Color.LightGray
    }

    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(34.dp)
            .background(color, RoundedCornerShape(4.dp))
            .clickable(enabled = !asiento.ocupado) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("${asiento.columna}", style = MaterialTheme.typography.caption, color = Color.White)
    }
}

@Composable
fun DialogoDatosComprador(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Datos") },
        text = { Column { OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }); OutlinedTextField(dni, { dni = it }, label = { Text("DNI") }) } },
        confirmButton = { Button(onClick = { onConfirm(nombre, dni) }) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}