package mx.edu.uttt.uniplan.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.data.database.entity.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(task: TaskEntity)

    @Update
    suspend fun actualizarTarea(task: TaskEntity)

    @Delete
    suspend fun eliminarTarea(task: TaskEntity)

    @Query("SELECT * FROM task WHERE id = :id")
    fun getItem(id: Int): Flow<TaskEntity>

    @Query("SELECT * FROM task ORDER BY id ASC")
    fun obtenerTareas(): Flow<List<TaskEntity>>
}
