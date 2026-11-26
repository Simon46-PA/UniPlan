package mx.edu.uttt.uniplan.data.mapper

import mx.edu.uttt.uniplan.data.database.entity.TaskEntity
import mx.edu.uttt.uniplan.domain.model.Task
import mx.edu.uttt.uniplan.domain.model.TaskStatus

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    title = title,
    subject = subject,
    dueDate = dueDate,
    status = runCatching { TaskStatus.valueOf(status) }.getOrDefault(TaskStatus.PENDING),
    dueToday = dueToday
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    subject = subject,
    dueDate = dueDate,
    status = status.name,
    dueToday = dueToday
)
