package ovh.gabrielhuav.flasklogin.data.api

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val username: String, // Cambiar a 'email' si tu Flask utiliza email
    val password: String
)

data class AuthResponse(
    val message: String,
    val status: String? = null
)

data class TaskDto(
    val id: Int? = null,
    val title: String,
    val description: String
)