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

// Estado visual
data class AsientoUI(val fila: Int, val columna: Int, var seleccionado: Boolean = false, var ocupado: Boolean = false)

@Composable
fun AsientosScreen(evento: Evento, onBack: () -> Unit) {
    val client = remember { NetworkClient() }
    val scope = rememberCoroutineScope()
    var mensaje by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) } // Control del Popup

    val asientos = remember {
        val lista = mutableListOf<AsientoUI>()
        val filas = evento.cantidadFilas ?: 10
        val cols = evento.cantidadColumnas ?: 10
        for (f in 1..filas) { for (c in 1..cols) { lista.add(AsientoUI(f, c)) } }
        mutableStateListOf(*lista.toTypedArray())
    }

    // DIÁLOGO EMERGENTE PARA PEDIR DATOS
    if (mostrarDialogo) {
        DialogoDatosComprador(
            onDismiss = { mostrarDialogo = false },
            onConfirm = { nombre, dni ->
                mostrarDialogo = false
                scope.launch {
                    mensaje = "Procesando venta..."
                    try {
                        val seleccionados = asientos.filter { it.seleccionado }
                        val listaVenta = seleccionados.map { AsientoVenta(it.fila, it.columna) }

                        val request = VentaRequest(
                            eventoId = evento.id,
                            precioVenta = seleccionados.size * (evento.precio ?: 0.0),
                            asientos = listaVenta,
                            nombreComprador = nombre,
                            dniComprador = dni
                        )

                        val resp = client.realizarVenta(request)

                        if (resp.resultado) {
                            mensaje = "¡COMPRA EXITOSA! ID: ${resp.ventaId}"
                            asientos.forEach { it.seleccionado = false }
                        } else {
                            mensaje = "Error: ${resp.descripcion}"
                        }
                    } catch (e: Exception) {
                        mensaje = "Fallo de conexión: ${e.message}"
                        e.printStackTrace()
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(evento.titulo ?: "Selección") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("<", color = Color.White, style = MaterialTheme.typography.h6) }
                }
            )
        },
        bottomBar = {
            val seleccionados = asientos.filter { it.seleccionado }
            if (seleccionados.isNotEmpty()) {
                Card(elevation = 8.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Total a pagar: $${seleccionados.size * (evento.precio ?: 0.0)}")
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { mostrarDialogo = true }, // Abre el popup
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("CONFIRMAR COMPRA")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (mensaje.isNotEmpty()) {
                Text(
                    text = mensaje,
                    color = if(mensaje.contains("EXITOSA")) Color(0xFF4CAF50) else Color.Red,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.h6
                )
            }

            Text("ESCENARIO", style = MaterialTheme.typography.caption)
            Box(Modifier.fillMaxWidth().height(4.dp).background(Color.Gray))
            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState())
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    val filas = evento.cantidadFilas ?: 10
                    val cols = evento.cantidadColumnas ?: 10

                    for (f in 1..filas) {
                        Row {
                            for (c in 1..cols) {
                                val index = asientos.indexOfFirst { it.fila == f && it.columna == c }
                                if (index != -1) {
                                    val asiento = asientos[index]
                                    AsientoItem(asiento) {
                                        asientos[index] = asiento.copy(seleccionado = !asiento.seleccionado)
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
        asiento.seleccionado -> Color(0xFF4CAF50)
        else -> Color.LightGray
    }
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(34.dp)
            .background(color, shape = RoundedCornerShape(4.dp))
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
        title = { Text("Datos del Comprador") },
        text = {
            Column {
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = dni, onValueChange = { dni = it }, label = { Text("DNI") })
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(nombre, dni) }) { Text("Finalizar Compra") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}