package mx.edu.uttt.uniplan.domain.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.domain.model.Task

interface TaskRepository {
    fun obtenerTareas(): Flow<List<Task>>
    suspend fun insertarTarea(task: Task)
    suspend fun actualizarTarea(task: Task)
    suspend fun eliminarTarea(task: Task)
}
