package mx.edu.uttt.uniplan.data.mapper

import mx.edu.uttt.uniplan.data.database.entity.ClassSessionEntity
import mx.edu.uttt.uniplan.data.database.entity.ClassSessionWithSubject
import mx.edu.uttt.uniplan.domain.model.ClassSession

fun ClassSessionWithSubject.toDomain(): ClassSession = ClassSession(
    id = session.id,
    subjectId = session.subjectId,
    subjectName = subject.name,
    room = subject.room,
    dayOfWeek = session.dayOfWeek,
    startTime = session.startTime,
    endTime = session.endTime
)

fun ClassSession.toEntity(): ClassSessionEntity = ClassSessionEntity(
    id = id,
    subjectId = subjectId,
    dayOfWeek = dayOfWeek,
    startTime = startTime,
    endTime = endTime
)
