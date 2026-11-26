package mx.edu.uttt.uniplan.domain.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.domain.model.ClassSession

interface ClassRepository {
    fun obtenerClases(): Flow<List<ClassSession>>
    suspend fun insertarClase(session: ClassSession)
    suspend fun actualizarClase(session: ClassSession)
    suspend fun eliminarClase(session: ClassSession)
}
