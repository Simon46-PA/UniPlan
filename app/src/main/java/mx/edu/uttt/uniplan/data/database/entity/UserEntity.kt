package mx.edu.uttt.uniplan.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Tabla "user". El correo es único; la contraseña nunca se guarda en texto plano (hash + sal). */
@Entity(tableName = "user", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val salt: String = ""
)
