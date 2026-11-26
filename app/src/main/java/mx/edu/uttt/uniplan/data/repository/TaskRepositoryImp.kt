package mx.edu.uttt.uniplan.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.edu.uttt.uniplan.data.database.dao.TaskDao
import mx.edu.uttt.uniplan.data.mapper.toDomain
import mx.edu.uttt.uniplan.data.mapper.toEntity
import mx.edu.uttt.uniplan.domain.model.Task
import mx.edu.uttt.uniplan.domain.repository.TaskRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImp @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun obtenerTareas(): Flow<List<Task>> {
        return taskDao.obtenerTareas().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun insertarTarea(task: Task) {
        taskDao.insertarTarea(task.toEntity())
    }

    override suspend fun actualizarTarea(task: Task) {
        taskDao.actualizarTarea(task.toEntity())
    }

    override suspend fun eliminarTarea(task: Task) {
        taskDao.eliminarTarea(task.toEntity())
    }
}
