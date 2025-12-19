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

const val TOKEN_MAESTRO = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTc2ODYwNjIyNCwiYXV0aCI6IlJPTEVfQURNSU4gUk9MRV9VU0VSIiwiaWF0IjoxNzY2MDE0MjI0LCJ1c2VySWQiOjF9.03wu6naREs5h7iv-QMHldigJvAq4nt8ioU2Su-zPV486Uq77nhmuypgBZ9qb_nHWKBOluoPqhJ5O0dU5hcLwfw"

@Serializable
data class Evento(
    val id: Long,
    val titulo: String? = null,
    val descripcion: String? = null,
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
data class VentaResponse(val resultado: Boolean, val ventaId: Long? = null, val descripcion: String? = null)

class NetworkClient {
    private val client = HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; prettyPrint = true }) }
    }

    private val baseUrl = "http://192.168.1.36:8080/api"

    suspend fun getEventos(): List<Evento> {
        val response = client.get("$baseUrl/endpoints/v1/eventos-resumidos") {
            header(HttpHeaders.Authorization, "Bearer $TOKEN_MAESTRO")
        }
        if (!response.status.isSuccess()) return emptyList()
        return response.body()
    }

    suspend fun realizarVenta(venta: VentaRequest): VentaResponse {
        val response = client.post("$baseUrl/endpoints/v1/realizar-venta") {
            header(HttpHeaders.Authorization, "Bearer $TOKEN_MAESTRO")
            contentType(ContentType.Application.Json)
            setBody(venta)
        }
        if (!response.status.isSuccess()) throw Exception("Error: ${response.status.value}")
        return response.body()
    }

    suspend fun getOcupados(eventoId: Long): List<AsientoVenta> {
        try {
            val response = client.get("$baseUrl/endpoints/v1/ocupados/$eventoId") {
                header(HttpHeaders.Authorization, "Bearer $TOKEN_MAESTRO")
            }
            if (response.status.isSuccess()) {
                return response.body()
            }
        } catch (e: Exception) {
            println("Error ocupados: ${e.message}")
        }
        return emptyList()
    }
}