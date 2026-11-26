package mx.edu.uttt.uniplan.domain.model

/** Una clase del horario semanal. dayOfWeek: 1 = lunes ... 7 = domingo (igual que java.time.DayOfWeek). */
data class ClassSession(
    val id: Int = 0,
    val subjectId: Int = 0,
    val subjectName: String = "",
    val room: String = "",
    val dayOfWeek: Int = 1,
    val startTime: String = "08:00",
    val endTime: String = "09:00"
)
