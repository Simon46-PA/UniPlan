package mx.edu.uttt.uniplan.data.mapper

import mx.edu.uttt.uniplan.data.database.entity.SubjectEntity
import mx.edu.uttt.uniplan.domain.model.Subject

fun SubjectEntity.toDomain(): Subject = Subject(id = id, name = name, room = room)

fun Subject.toEntity(): SubjectEntity = SubjectEntity(id = id, name = name, room = room)
