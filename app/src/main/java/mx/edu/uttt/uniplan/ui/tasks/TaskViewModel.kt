package mx.edu.uttt.uniplan.ui.tasks

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.TASK_TITLE_MAX_LENGTH
import mx.edu.uttt.uniplan.domain.model.Task
import mx.edu.uttt.uniplan.domain.model.TaskStatus
import mx.edu.uttt.uniplan.domain.repository.TaskRepository
import mx.edu.uttt.uniplan.ui.common.launchSafely
import javax.inject.Inject

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    @StringRes val errorRes: Int? = null
)

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val errorRes = MutableStateFlow<Int?>(null)

    /** Estado que observa la UI (Observer sobre Flow de Room). */
    val uiState: StateFlow<TasksUiState> = combine(repository.obtenerTareas(), errorRes) { tasks, error ->
        TasksUiState(tasks, error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TasksUiState()
    )

    private var lastDeleted: Task? = null

    fun agregarTarea(title: String, subject: String, dueDate: String) {
        val trimTitle = title.trim()
        if (trimTitle.isEmpty() || trimTitle.length > TASK_TITLE_MAX_LENGTH) {
            errorRes.value = R.string.error_task_title
            return
        }
        launchSafely(onError = { errorRes.value = R.string.error_save }) {
            repository.insertarTarea(
                Task(
                    title = trimTitle,
                    subject = subject.trim(),
                    dueDate = dueDate,
                    status = TaskStatus.PENDING
                )
            )
        }
    }

    fun alternarCompletada(task: Task) {
        val newStatus =
            if (task.status == TaskStatus.COMPLETED) TaskStatus.PENDING else TaskStatus.COMPLETED
        launchSafely(onError = { errorRes.value = R.string.error_save }) {
            repository.actualizarTarea(task.copy(status = newStatus))
        }
    }

    fun eliminarTarea(task: Task) {
        lastDeleted = task
        launchSafely(onError = { errorRes.value = R.string.error_delete }) {
            repository.eliminarTarea(task)
        }
    }

    fun deshacerEliminar() {
        val task = lastDeleted ?: return
        lastDeleted = null
        launchSafely(onError = { errorRes.value = R.string.error_save }) {
            repository.insertarTarea(task)
        }
    }

    fun clearError() {
        errorRes.update { null }
    }
}
