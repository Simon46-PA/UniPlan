package mx.edu.uttt.uniplan.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import mx.edu.uttt.uniplan.data.database.dao.ClassSessionDao
import mx.edu.uttt.uniplan.data.database.dao.SubjectDao
import mx.edu.uttt.uniplan.data.database.dao.TaskDao
import mx.edu.uttt.uniplan.data.database.dao.UserDao
import mx.edu.uttt.uniplan.data.database.entity.ClassSessionEntity
import mx.edu.uttt.uniplan.data.database.entity.SubjectEntity
import mx.edu.uttt.uniplan.data.database.entity.TaskEntity
import mx.edu.uttt.uniplan.data.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TaskEntity::class,
        SubjectEntity::class,
        ClassSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao
    abstract fun subjectDao(): SubjectDao
    abstract fun classSessionDao(): ClassSessionDao
}
