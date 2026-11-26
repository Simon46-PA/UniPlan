package mx.edu.uttt.uniplan.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

/** Tabla "class_session": relación N:1 con "subject". Al borrar una materia se borran sus clases. */
@Entity(
    tableName = "class_session",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("subjectId")]
)
data class ClassSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val subjectId: Int = 0,
    val dayOfWeek: Int = 1,
    val startTime: String = "08:00",
    val endTime: String = "09:00"
)

/** Clase junto con su materia (resultado de la relación). */
data class ClassSessionWithSubject(
    @Embedded val session: ClassSessionEntity,
    @Relation(parentColumn = "subjectId", entityColumn = "id")
    val subject: SubjectEntity
)
