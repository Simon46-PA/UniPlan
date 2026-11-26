package mx.edu.uttt.uniplan.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.edu.uttt.uniplan.data.database.dao.SubjectDao
import mx.edu.uttt.uniplan.data.mapper.toDomain
import mx.edu.uttt.uniplan.data.mapper.toEntity
import mx.edu.uttt.uniplan.domain.model.Subject
import mx.edu.uttt.uniplan.domain.repository.SubjectRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubjectRepositoryImp @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository {

    override fun obtenerMaterias(): Flow<List<Subject>> =
        subjectDao.obtenerMaterias().map { list -> list.map { it.toDomain() } }

    override suspend fun insertarMateria(subject: Subject) = subjectDao.insertarMateria(subject.toEntity())

    override suspend fun actualizarMateria(subject: Subject) = subjectDao.actualizarMateria(subject.toEntity())

    override suspend fun eliminarMateria(subject: Subject) = subjectDao.eliminarMateria(subject.toEntity())
}
