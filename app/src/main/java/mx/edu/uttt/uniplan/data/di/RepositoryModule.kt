package mx.edu.uttt.uniplan.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import mx.edu.uttt.uniplan.data.repository.ClassRepositoryImp
import mx.edu.uttt.uniplan.data.repository.SubjectRepositoryImp
import mx.edu.uttt.uniplan.data.repository.TaskRepositoryImp
import mx.edu.uttt.uniplan.data.repository.UserRepositoryImp
import mx.edu.uttt.uniplan.domain.repository.ClassRepository
import mx.edu.uttt.uniplan.domain.repository.SubjectRepository
import mx.edu.uttt.uniplan.domain.repository.TaskRepository
import mx.edu.uttt.uniplan.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepository: TaskRepositoryImp
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepository: UserRepositoryImp
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindSubjectRepository(
        subjectRepository: SubjectRepositoryImp
    ): SubjectRepository

    @Binds
    @Singleton
    abstract fun bindClassRepository(
        classRepository: ClassRepositoryImp
    ): ClassRepository
}
