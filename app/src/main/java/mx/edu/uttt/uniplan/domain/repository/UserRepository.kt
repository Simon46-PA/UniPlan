package mx.edu.uttt.uniplan.domain.repository

import mx.edu.uttt.uniplan.domain.model.AuthResult
import mx.edu.uttt.uniplan.domain.model.User

interface UserRepository {
    suspend fun registrar(name: String, email: String, password: String): AuthResult
    suspend fun iniciarSesion(email: String, password: String): AuthResult
    suspend fun actualizarNombre(user: User, newName: String): User
    suspend fun eliminarCuenta(user: User)
}
