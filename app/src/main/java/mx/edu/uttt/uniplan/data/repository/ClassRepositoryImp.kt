package mx.edu.uttt.uniplan.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.edu.uttt.uniplan.data.database.dao.ClassSessionDao
import mx.edu.uttt.uniplan.data.mapper.toDomain
import mx.edu.uttt.uniplan.data.mapper.toEntity
import mx.edu.uttt.uniplan.domain.model.ClassSession
import mx.edu.uttt.uniplan.domain.repository.ClassRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClassRepositoryImp @Inject constructor(
    private val classDao: ClassSessionDao
) : ClassRepository {

    override fun obtenerClases(): Flow<List<ClassSession>> =
        classDao.obtenerClases().map { list -> list.map { it.toDomain() } }

    override suspend fun insertarClase(session: ClassSession) = classDao.insertarClase(session.toEntity())

    override suspend fun actualizarClase(session: ClassSession) = classDao.actualizarClase(session.toEntity())

    override suspend fun eliminarClase(session: ClassSession) = classDao.eliminarClase(session.toEntity())
}
