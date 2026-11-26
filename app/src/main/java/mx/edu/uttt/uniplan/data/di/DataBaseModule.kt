package mx.edu.uttt.uniplan.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mx.edu.uttt.uniplan.data.database.AppDatabase
import mx.edu.uttt.uniplan.data.database.dao.ClassSessionDao
import mx.edu.uttt.uniplan.data.database.dao.SubjectDao
import mx.edu.uttt.uniplan.data.database.dao.TaskDao
import mx.edu.uttt.uniplan.data.database.dao.UserDao
import mx.edu.uttt.uniplan.data.database.insertSeedClasses
import mx.edu.uttt.uniplan.data.database.insertSeedSubjects
import mx.edu.uttt.uniplan.data.database.insertSeedTasks
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun getInstance(@ApplicationContext context: Context): AppDatabase {
        return synchronized(this) {
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "uniplan.db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        insertSeedTasks(context, db)
                        insertSeedSubjects(context, db)
                        insertSeedClasses(db)
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideClassSessionDao(database: AppDatabase): ClassSessionDao = database.classSessionDao()
}
