package mx.edu.uttt.uniplan.ui.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.ClassSession
import mx.edu.uttt.uniplan.domain.model.Subject
import mx.edu.uttt.uniplan.domain.validation.isValidTime
import mx.edu.uttt.uniplan.ui.theme.UniPlanTheme

/** Nombres de los días: el índice + 1 coincide con ClassSession.dayOfWeek (1 = lunes). */
val dayNames: List<Int> = listOf(
    R.string.day_1, R.string.day_2, R.string.day_3, R.string.day_4,
    R.string.day_5, R.string.day_6, R.string.day_7
)

/** Pestaña Calendario/Horario: clases agrupadas por día con editar y eliminar. */
@Composable
fun ScheduleScreen(
    classes: List<ClassSession>,
    subjects: List<Subject>,
    showAddDialog: Boolean,
    onAddDialogDismiss: () -> Unit,
    onSave: (id: Int, subjectId: Int, day: Int, start: String, end: String) -> Unit,
    onDelete: (ClassSession) -> Unit,
    contentPadding: PaddingValues
) {
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    val editing = classes.firstOrNull { it.id == editingId }

    if (showAddDialog) {
        ClassFormDialog(
            initial = null,
            subjects = subjects,
            onDismiss = onAddDialogDismiss,
            onSave = { subjectId, day, start, end ->
                onSave(0, subjectId, day, start, end)
                onAddDialogDismiss()
            }
        )
    }
    if (editing != null) {
        ClassFormDialog(
            initial = editing,
            subjects = subjects,
            onDismiss = { editingId = null },
            onSave = { subjectId, day, start, end ->
                onSave(editing.id, subjectId, day, start, end)
                editingId = null
            }
        )
    }

    if (classes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.schedule_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val screenPadding = dimensionResource(R.dimen.screen_padding)
    val roomTbd = stringResource(R.string.room_tbd)
    val byDay = classes.groupBy { it.dayOfWeek }.toSortedMap()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = screenPadding,
            end = screenPadding,
            top = contentPadding.calculateTopPadding() + screenPadding,
            bottom = contentPadding.calculateBottomPadding() + screenPadding
        ),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
    ) {
        byDay.forEach { (day, dayClasses) ->
            item(key = "day-$day") {
                Text(
                    text = stringResource(dayNames[(day - 1).coerceIn(0, 6)]),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            items(dayClasses, key = { it.id }) { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(session.subjectName, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = stringResource(
                                    R.string.class_item_info,
                                    session.startTime,
                                    session.endTime,
                                    session.room.ifBlank { roomTbd }
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { editingId = session.id }) {
                            Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { onDelete(session) }) {
                            Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassFormDialog(
    initial: ClassSession?,
    subjects: List<Subject>,
    onDismiss: () -> Unit,
    onSave: (subjectId: Int, day: Int, start: String, end: String) -> Unit
) {
    var subjectId by rememberSaveable(initial?.id) { mutableStateOf(initial?.subjectId ?: 0) }
    var day by rememberSaveable(initial?.id) { mutableStateOf(initial?.dayOfWeek ?: 1) }
    var start by rememberSaveable(initial?.id) { mutableStateOf(initial?.startTime ?: "08:00") }
    var end by rememberSaveable(initial?.id) { mutableStateOf(initial?.endTime ?: "09:00") }
    var subjectMenu by remember { mutableStateOf(false) }
    var dayMenu by remember { mutableStateOf(false) }

    val startValid = start.isValidTime()
    val endValid = end.isValidTime()
    val orderValid = !startValid || !endValid || end.trim() > start.trim()
    val canSave = subjectId > 0 && startValid && endValid && orderValid

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(if (initial == null) R.string.class_dialog_add_title else R.string.class_dialog_edit_title))
        },
        text = {
            if (subjects.isEmpty()) {
                Text(stringResource(R.string.class_need_subject))
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                    Box {
                        OutlinedButton(onClick = { subjectMenu = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                subjects.firstOrNull { it.id == subjectId }?.name
                                    ?: stringResource(R.string.class_choose_subject)
                            )
                        }
                        DropdownMenu(expanded = subjectMenu, onDismissRequest = { subjectMenu = false }) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text(subject.name) },
                                    onClick = {
                                        subjectId = subject.id
                                        subjectMenu = false
                                    }
                                )
                            }
                        }
                    }
                    Box {
                        OutlinedButton(onClick = { dayMenu = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(dayNames[(day - 1).coerceIn(0, 6)]))
                        }
                        DropdownMenu(expanded = dayMenu, onDismissRequest = { dayMenu = false }) {
                            dayNames.forEachIndexed { index, nameRes ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(nameRes)) },
                                    onClick = {
                                        day = index + 1
                                        dayMenu = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = start,
                        onValueChange = { if (it.length <= 5) start = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.class_field_start)) },
                        isError = !startValid,
                        supportingText = { if (!startValid) Text(stringResource(R.string.class_error_time_format)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = end,
                        onValueChange = { if (it.length <= 5) end = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.class_field_end)) },
                        isError = !endValid || !orderValid,
                        supportingText = {
                            if (!endValid) {
                                Text(stringResource(R.string.class_error_time_format))
                            } else if (!orderValid) {
                                Text(stringResource(R.string.class_error_time_order))
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(subjectId, day, start.trim(), end.trim()) },
                enabled = canSave
            ) { Text(stringResource(R.string.dialog_save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenPreview() {
    UniPlanTheme {
        ScheduleScreen(
            classes = listOf(ClassSession(1, 1, "Desarrollo de aplicaciones móviles", "Laboratorio 3", 1, "08:00", "10:00")),
            subjects = listOf(Subject(1, "Desarrollo de aplicaciones móviles", "Laboratorio 3")),
            showAddDialog = false,
            onAddDialogDismiss = {},
            onSave = { _, _, _, _, _ -> },
            onDelete = {},
            contentPadding = PaddingValues()
        )
    }
}
