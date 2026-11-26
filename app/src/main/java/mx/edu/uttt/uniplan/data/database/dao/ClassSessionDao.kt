package mx.edu.uttt.uniplan.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.data.database.entity.ClassSessionEntity
import mx.edu.uttt.uniplan.data.database.entity.ClassSessionWithSubject

@Dao
interface ClassSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarClase(session: ClassSessionEntity)

    @Update
    suspend fun actualizarClase(session: ClassSessionEntity)

    @Delete
    suspend fun eliminarClase(session: ClassSessionEntity)

    @Transaction
    @Query("SELECT * FROM class_session ORDER BY dayOfWeek ASC, startTime ASC")
    fun obtenerClases(): Flow<List<ClassSessionWithSubject>>
}
