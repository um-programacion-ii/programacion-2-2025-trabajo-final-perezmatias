package ar.edu.um.movil

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class NetworkClient {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    private val baseUrl = "http://10.0.2.2:8080/api"

    suspend fun login(usuario: String, clave: String): String {
        val url = "$baseUrl/authenticate"
        val requestData = LoginRequest(username = usuario, password = clave)

        println("📡 Login en: $url")
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestData)
        }

        if (!response.status.isSuccess()) {
            throw Exception("Error Login: ${response.status.value}")
        }

        val data: LoginResponse = response.body()
        return data.idToken
    }

    suspend fun getEventos(token: String): List<Evento> {
        val url = "$baseUrl/endpoints/v1/eventos-resumidos"
        println("📡 Buscando eventos en: $url")

        val response = client.get(url) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        if (!response.status.isSuccess()) {
            throw Exception("Error Eventos: ${response.status.value}")
        }

        return response.body()
    }
}