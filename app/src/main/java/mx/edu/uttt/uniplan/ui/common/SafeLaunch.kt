package mx.edu.uttt.uniplan.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Función de extensión de ViewModel: ejecuta una operación suspendida (Room) y, si falla,
 * avisa con [onError] en lugar de cerrar la app.
 */
fun ViewModel.launchSafely(onError: () -> Unit, block: suspend () -> Unit): Job =
    viewModelScope.launch {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            onError()
        }
    }
