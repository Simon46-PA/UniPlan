package mx.edu.uttt.uniplan.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import mx.edu.uttt.uniplan.data.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(user: UserEntity): Long

    @Update
    suspend fun actualizarUsuario(user: UserEntity)

    @Delete
    suspend fun eliminarUsuario(user: UserEntity)

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UserEntity?

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): UserEntity?
}
