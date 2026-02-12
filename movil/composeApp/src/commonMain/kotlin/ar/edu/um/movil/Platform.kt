package ar.edu.um.movil

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform