package mx.edu.uttt.uniplan.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subject")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val room: String = ""
)
