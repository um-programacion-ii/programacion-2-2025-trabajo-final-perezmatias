package ar.edu.um.movil

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)

@Serializable
data class LoginResponse(
    @SerialName("id_token") val idToken: String
)

@Serializable
data class Evento(
    val id: Long,
    val titulo: String? = null,
    val descripcion: String? = null,
    val fechaHora: String? = null, // Viene como String ISO
    val precio: Double? = null
)