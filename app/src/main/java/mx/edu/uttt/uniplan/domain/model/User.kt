package mx.edu.uttt.uniplan.domain.model

const val PASSWORD_MIN_LENGTH = 6

data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = ""
)

enum class AuthError { INVALID_CREDENTIALS, EMAIL_TAKEN, DATABASE }

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Failure(val error: AuthError) : AuthResult
}
