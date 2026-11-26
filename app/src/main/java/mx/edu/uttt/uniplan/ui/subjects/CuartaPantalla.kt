package mx.edu.uttt.uniplan.ui.subjects

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.SUBJECT_NAME_MAX_LENGTH
import mx.edu.uttt.uniplan.domain.model.Subject
import mx.edu.uttt.uniplan.ui.theme.UniPlanTheme

/** Pestaña Materias: lista con editar y eliminar; el botón "+" (en el Scaffold) abre el diálogo de alta. */
@Composable
fun SubjectsScreen(
    subjects: List<Subject>,
    showAddDialog: Boolean,
    onAddDialogDismiss: () -> Unit,
    onSave: (id: Int, name: String, room: String) -> Unit,
    onDelete: (Subject) -> Unit,
    contentPadding: PaddingValues
) {
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Int?>(null) }
    val editing = subjects.firstOrNull { it.id == editingId }
    val deleting = subjects.firstOrNull { it.id == deletingId }

    if (showAddDialog) {
        SubjectFormDialog(
            initial = null,
            onDismiss = onAddDialogDismiss,
            onSave = { name, room ->
                onSave(0, name, room)
                onAddDialogDismiss()
            }
        )
    }
    if (editing != null) {
        SubjectFormDialog(
            initial = editing,
            onDismiss = { editingId = null },
            onSave = { name, room ->
                onSave(editing.id, name, room)
                editingId = null
            }
        )
    }
    if (deleting != null) {
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text(stringResource(R.string.subject_delete_title)) },
            text = { Text(stringResource(R.string.subject_delete_message, deleting.name)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(deleting)
                    deletingId = null
                }) { Text(stringResource(R.string.dialog_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { deletingId = null }) { Text(stringResource(R.string.dialog_cancel)) }
            }
        )
    }

    if (subjects.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.subjects_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val screenPadding = dimensionResource(R.dimen.screen_padding)
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
        items(subjects, key = { it.id }) { subject ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Row(
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_school),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = screenPadding)
                    ) {
                        Text(subject.name, style = MaterialTheme.typography.bodyLarge)
                        if (subject.room.isNotBlank()) {
                            Text(
                                subject.room,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = { editingId = subject.id }) {
                        Icon(painterResource(R.drawable.ic_edit), contentDescription = stringResource(R.string.action_edit))
                    }
                    IconButton(onClick = { deletingId = subject.id }) {
                        Icon(painterResource(R.drawable.ic_delete), contentDescription = stringResource(R.string.action_delete))
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectFormDialog(
    initial: Subject?,
    onDismiss: () -> Unit,
    onSave: (name: String, room: String) -> Unit
) {
    var name by rememberSaveable(initial?.id) { mutableStateOf(initial?.name ?: "") }
    var room by rememberSaveable(initial?.id) { mutableStateOf(initial?.room ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                stringResource(
                    if (initial == null) R.string.subject_dialog_add_title else R.string.subject_dialog_edit_title
                )
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= SUBJECT_NAME_MAX_LENGTH) name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.subject_field_name)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.spacing_small)),
                    label = { Text(stringResource(R.string.subject_field_room)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(name, room) }, enabled = name.isNotBlank()) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_cancel)) }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SubjectsScreenPreview() {
    UniPlanTheme {
        SubjectsScreen(
            subjects = listOf(Subject(1, "Estructura de datos", "Salón B-12")),
            showAddDialog = false,
            onAddDialogDismiss = {},
            onSave = { _, _, _ -> },
            onDelete = {},
            contentPadding = PaddingValues()
        )
    }
}
