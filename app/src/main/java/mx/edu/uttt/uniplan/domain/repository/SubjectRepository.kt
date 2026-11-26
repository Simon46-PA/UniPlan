package mx.edu.uttt.uniplan.domain.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.domain.model.Subject

interface SubjectRepository {
    fun obtenerMaterias(): Flow<List<Subject>>
    suspend fun insertarMateria(subject: Subject)
    suspend fun actualizarMateria(subject: Subject)
    suspend fun eliminarMateria(subject: Subject)
}
