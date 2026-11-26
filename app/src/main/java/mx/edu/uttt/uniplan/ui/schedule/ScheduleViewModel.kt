package mx.edu.uttt.uniplan.ui.schedule

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.ClassSession
import mx.edu.uttt.uniplan.domain.model.Subject
import mx.edu.uttt.uniplan.domain.repository.ClassRepository
import mx.edu.uttt.uniplan.domain.repository.SubjectRepository
import mx.edu.uttt.uniplan.domain.validation.isValidTime
import mx.edu.uttt.uniplan.ui.common.launchSafely
import javax.inject.Inject

data class ScheduleUiState(
    val classes: List<ClassSession> = emptyList(),
    val subjects: List<Subject> = emptyList(),
    @StringRes val errorRes: Int? = null
)

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    subjectRepository: SubjectRepository
) : ViewModel() {

    private val errorRes = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<ScheduleUiState> = combine(
        classRepository.obtenerClases(),
        subjectRepository.obtenerMaterias(),
        errorRes
    ) { classes, subjects, error ->
        ScheduleUiState(classes, subjects, error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ScheduleUiState()
    )

    /** id == 0 crea una clase nueva; id > 0 actualiza la existente. */
    fun guardarClase(id: Int, subjectId: Int, dayOfWeek: Int, startTime: String, endTime: String) {
        val start = startTime.trim()
        val end = endTime.trim()
        if (subjectId <= 0 || !start.isValidTime() || !end.isValidTime() || end <= start) {
            errorRes.value = R.string.error_class_invalid
            return
        }
        val session = ClassSession(
            id = id,
            subjectId = subjectId,
            dayOfWeek = dayOfWeek,
            startTime = start,
            endTime = end
        )
        launchSafely(onError = { errorRes.value = R.string.error_save }) {
            if (id == 0) classRepository.insertarClase(session) else classRepository.actualizarClase(session)
        }
    }

    fun eliminarClase(session: ClassSession) {
        launchSafely(onError = { errorRes.value = R.string.error_delete }) {
            classRepository.eliminarClase(session)
        }
    }

    fun clearError() {
        errorRes.value = null
    }
}
