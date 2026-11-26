package mx.edu.uttt.uniplan.ui.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.TASK_TITLE_MAX_LENGTH

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, subject: String) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var subject by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_add_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= TASK_TITLE_MAX_LENGTH) title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.dialog_field_title)) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensionResource(R.dimen.spacing_small)),
                    label = { Text(stringResource(R.string.dialog_field_subject)) },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(title, subject) },
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(R.string.dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_cancel))
            }
        }
    )
}
