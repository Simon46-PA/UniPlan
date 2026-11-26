package mx.edu.uttt.uniplan.domain.validation

private val TIME_REGEX = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

/** Función de extensión: true si el texto tiene formato HH:mm de 24 horas (por ejemplo, 08:30). */
fun String.isValidTime(): Boolean = TIME_REGEX.matches(this.trim())
