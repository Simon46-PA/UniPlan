package mx.edu.uttt.uniplan.domain.model

const val SUBJECT_NAME_MAX_LENGTH = 60

data class Subject(
    val id: Int = 0,
    val name: String = "",
    val room: String = ""
)
