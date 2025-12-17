package ar.edu.um.movil

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// --- TOKEN MAESTRO ---
// Token válido hasta Enero 2026 (aprox) con permisos de ADMIN
const val TOKEN_MAESTRO = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc2ODYwNjIyNCwiYXV0aCI6IlJPTEVfQURNSU4gUk9MRV9VU0VSIiwiaWF0IjoxNzY2MDE0MjI0LCJ1c2VySWQiOjF9.03wu6naREs5h7iv-QMHldigJvAq4nt8ioU2Su-zPV486Uq77nhmuypgBZ9qb_nHWKBOluoPqhJ5O0dU5hcLwfw"

// --- DTOs ---

@Serializable
data class Evento(
    val id: Long,
    val titulo: String? = null,
    val descripcion: String? = null,
    val fechaHora: String? = null,
    val precio: Double? = null,
    val cantidadFilas: Int? = null,
    val cantidadColumnas: Int? = null
)

@Serializable
data class VentaRequest(
    val eventoId: Long,
    val precioVenta: Double,
    val asientos: List<AsientoVenta>,
    val nombreComprador: String,
    val dniComprador: String
)

@Serializable
data class AsientoVenta(
    val fila: Int,
    val columna: Int,
    val persona: String = "Usuario App"
)

@Serializable
data class VentaResponse(
    val resultado: Boolean,
    val ventaId: Long? = null,
    val descripcion: String? = null
)

class NetworkClient {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; prettyPrint = true })
        }
    }

    // ⚠️ REVISA QUE ESTA IP SEA LA DE TU PC
    private val baseUrl = "http://192.168.1.36:8080/api"

    // Obtener Eventos (Usa el Token Maestro fijo)
    suspend fun getEventos(): List<Evento> {
        val url = "$baseUrl/endpoints/v1/eventos-resumidos"
        println("📡 Buscando eventos...")
        val response = client.get(url) {
            header(HttpHeaders.Authorization, "Bearer $TOKEN_MAESTRO")
        }
        if (!response.status.isSuccess()) throw Exception("Error Eventos: ${response.status.value}")
        return response.body()
    }

    // Realizar Venta
    suspend fun realizarVenta(venta: VentaRequest): VentaResponse {
        val url = "$baseUrl/endpoints/v1/realizar-venta"
        println("💰 Enviando venta de: ${venta.nombreComprador}")
        val response = client.post(url) {
            header(HttpHeaders.Authorization, "Bearer $TOKEN_MAESTRO")
            contentType(ContentType.Application.Json)
            setBody(venta)
        }
        if (!response.status.isSuccess()) throw Exception("Error Venta: ${response.status.value}")
        return response.body()
    }
}