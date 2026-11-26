package mx.edu.uttt.uniplan.ui.auth

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.AuthError
import mx.edu.uttt.uniplan.domain.model.AuthResult
import mx.edu.uttt.uniplan.domain.model.User
import mx.edu.uttt.uniplan.domain.repository.UserRepository
import mx.edu.uttt.uniplan.ui.common.launchSafely
import javax.inject.Inject

/** Estado único de la pantalla (UDF): la UI solo lo observa y envía eventos al ViewModel. */
data class AuthUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = false,
    @StringRes val errorRes: Int? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        launchAuth { repository.iniciarSesion(email, password) }
    }

    fun register(name: String, email: String, password: String) {
        launchAuth { repository.registrar(name, email, password) }
    }

    private fun launchAuth(call: suspend () -> AuthResult) {
        _uiState.update { it.copy(isLoading = true, errorRes = null) }
        launchSafely(onError = { _uiState.update { it.copy(isLoading = false, errorRes = R.string.error_database) } }) {
            when (val result = call()) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(isLoading = false, currentUser = result.user, errorRes = null)
                }
                is AuthResult.Failure -> _uiState.update {
                    it.copy(isLoading = false, errorRes = result.error.messageRes())
                }
            }
        }
    }

    fun logout() {
        _uiState.value = AuthUiState()
    }

    fun updateName(newName: String) {
        val user = _uiState.value.currentUser ?: return
        if (newName.isBlank()) return
        launchSafely(onError = { _uiState.update { it.copy(errorRes = R.string.error_save) } }) {
            val updated = repository.actualizarNombre(user, newName)
            _uiState.update { it.copy(currentUser = updated) }
        }
    }

    fun deleteAccount() {
        val user = _uiState.value.currentUser ?: return
        launchSafely(onError = { _uiState.update { it.copy(errorRes = R.string.error_delete) } }) {
            repository.eliminarCuenta(user)
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorRes = null) }
    }
}

@StringRes
private fun AuthError.messageRes(): Int = when (this) {
    AuthError.INVALID_CREDENTIALS -> R.string.login_error_invalid_credentials
    AuthError.EMAIL_TAKEN -> R.string.register_error_email_taken
    AuthError.DATABASE -> R.string.error_database
}
