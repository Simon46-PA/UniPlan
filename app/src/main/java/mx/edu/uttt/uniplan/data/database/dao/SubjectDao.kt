package mx.edu.uttt.uniplan.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.edu.uttt.uniplan.data.database.entity.SubjectEntity

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMateria(subject: SubjectEntity)

    @Update
    suspend fun actualizarMateria(subject: SubjectEntity)

    @Delete
    suspend fun eliminarMateria(subject: SubjectEntity)

    @Query("SELECT * FROM subject ORDER BY name COLLATE NOCASE ASC")
    fun obtenerMaterias(): Flow<List<SubjectEntity>>
}
