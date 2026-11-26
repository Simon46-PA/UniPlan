package mx.edu.uttt.uniplan.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mx.edu.uttt.uniplan.data.database.dao.UserDao
import mx.edu.uttt.uniplan.data.database.entity.UserEntity
import mx.edu.uttt.uniplan.data.mapper.toDomain
import mx.edu.uttt.uniplan.data.security.PasswordHasher
import mx.edu.uttt.uniplan.domain.model.AuthError
import mx.edu.uttt.uniplan.domain.model.AuthResult
import mx.edu.uttt.uniplan.domain.model.User
import mx.edu.uttt.uniplan.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImp @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun registrar(name: String, email: String, password: String): AuthResult =
        withContext(Dispatchers.Default) {
            try {
                val normalizedEmail = email.trim().lowercase()
                if (userDao.buscarPorEmail(normalizedEmail) != null) {
                    return@withContext AuthResult.Failure(AuthError.EMAIL_TAKEN)
                }
                val salt = PasswordHasher.newSalt()
                val entity = UserEntity(
                    name = name.trim(),
                    email = normalizedEmail,
                    passwordHash = PasswordHasher.hash(password, salt),
                    salt = salt
                )
                val newId = userDao.insertarUsuario(entity)
                AuthResult.Success(entity.copy(id = newId.toInt()).toDomain())
            } catch (e: Exception) {
                AuthResult.Failure(AuthError.DATABASE)
            }
        }

    override suspend fun iniciarSesion(email: String, password: String): AuthResult =
        withContext(Dispatchers.Default) {
            try {
                val entity = userDao.buscarPorEmail(email.trim().lowercase())
                if (entity != null && PasswordHasher.matches(password, entity.salt, entity.passwordHash)) {
                    AuthResult.Success(entity.toDomain())
                } else {
                    // Mismo error para correo inexistente y contraseña incorrecta (no revela cuál falló).
                    AuthResult.Failure(AuthError.INVALID_CREDENTIALS)
                }
            } catch (e: Exception) {
                AuthResult.Failure(AuthError.DATABASE)
            }
        }

    override suspend fun actualizarNombre(user: User, newName: String): User {
        val entity = userDao.buscarPorId(user.id) ?: return user
        val updated = entity.copy(name = newName.trim())
        userDao.actualizarUsuario(updated)
        return updated.toDomain()
    }

    override suspend fun eliminarCuenta(user: User) {
        val entity = userDao.buscarPorId(user.id) ?: return
        userDao.eliminarUsuario(entity)
    }
}
