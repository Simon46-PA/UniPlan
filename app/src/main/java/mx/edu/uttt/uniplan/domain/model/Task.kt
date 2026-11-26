package mx.edu.uttt.uniplan.domain.model

const val TASK_TITLE_MAX_LENGTH = 60

enum class TaskStatus { PENDING, IN_PROGRESS, COMPLETED }

data class Task(
    val id: Int = 0,
    val title: String = "",
    val subject: String = "",
    val dueDate: String = "",
    val status: TaskStatus = TaskStatus.PENDING,
    val dueToday: Boolean = false
)
