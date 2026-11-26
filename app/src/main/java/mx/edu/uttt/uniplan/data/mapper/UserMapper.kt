package mx.edu.uttt.uniplan.data.mapper

import mx.edu.uttt.uniplan.data.database.entity.UserEntity
import mx.edu.uttt.uniplan.domain.model.User

fun UserEntity.toDomain(): User = User(id = id, name = name, email = email)
