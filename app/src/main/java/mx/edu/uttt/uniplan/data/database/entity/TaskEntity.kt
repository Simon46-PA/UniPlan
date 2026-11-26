package mx.edu.uttt.uniplan.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import mx.edu.uttt.uniplan.domain.model.TASK_TITLE_MAX_LENGTH

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val subject: String = "",
    val dueDate: String = "",
    val status: String = "",
    val dueToday: Boolean = false
) {
    init {
        require(title.length <= TASK_TITLE_MAX_LENGTH) {
            "El título no puede tener más de $TASK_TITLE_MAX_LENGTH caracteres"
        }
    }
}
