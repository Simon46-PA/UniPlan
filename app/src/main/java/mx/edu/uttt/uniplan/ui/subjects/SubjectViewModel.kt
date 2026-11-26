package mx.edu.uttt.uniplan.ui.subjects

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
import mx.edu.uttt.uniplan.domain.model.SUBJECT_NAME_MAX_LENGTH
import mx.edu.uttt.uniplan.domain.model.Subject
import mx.edu.uttt.uniplan.domain.repository.SubjectRepository
import mx.edu.uttt.uniplan.ui.common.launchSafely
import javax.inject.Inject

data class SubjectsUiState(
    val subjects: List<Subject> = emptyList(),
    @StringRes val errorRes: Int? = null
)

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val repository: SubjectRepository
) : ViewModel() {

    private val errorRes = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<SubjectsUiState> = combine(repository.obtenerMaterias(), errorRes) { subjects, error ->
        SubjectsUiState(subjects, error)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SubjectsUiState()
    )

    /** id == 0 crea una materia nueva; id > 0 actualiza la existente. */
    fun guardarMateria(id: Int, name: String, room: String) {
        val cleanName = name.trim()
        if (cleanName.isEmpty() || cleanName.length > SUBJECT_NAME_MAX_LENGTH) {
            errorRes.value = R.string.error_subject_name
            return
        }
        val subject = Subject(id = id, name = cleanName, room = room.trim())
        launchSafely(onError = { errorRes.value = R.string.error_save }) {
            if (id == 0) repository.insertarMateria(subject) else repository.actualizarMateria(subject)
        }
    }

    fun eliminarMateria(subject: Subject) {
        launchSafely(onError = { errorRes.value = R.string.error_delete }) {
            repository.eliminarMateria(subject)
        }
    }

    fun clearError() {
        errorRes.value = null
    }
}
