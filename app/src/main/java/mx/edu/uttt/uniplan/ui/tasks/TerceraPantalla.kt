package mx.edu.uttt.uniplan.ui.tasks

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.Task
import mx.edu.uttt.uniplan.domain.model.TaskStatus

private enum class TaskFilter(@StringRes val label: Int) {
    ALL(R.string.filter_all),
    PENDING(R.string.filter_pending),
    IN_PROGRESS(R.string.filter_in_progress),
    COMPLETED(R.string.filter_completed),
    SUBJECT_MOBILE(R.string.filter_subject_mobile)
}

/** Pantalla 3. Mis tareas: búsqueda, chips de filtro, lista con casilla y deslizar para eliminar. */
@Composable
fun TasksScreen(
    tasks: List<Task>,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onUndoDelete: () -> Unit,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues
) {
    val scope = rememberCoroutineScope()
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(TaskFilter.ALL) }
    val deletedMessage = stringResource(R.string.tasks_deleted)
    val undoLabel = stringResource(R.string.tasks_undo)
    val mobileSubject = stringResource(R.string.subject_mobile)

    val visibleTasks = tasks.filter { task ->
        val matchesFilter = when (filter) {
            TaskFilter.ALL -> true
            TaskFilter.PENDING -> task.status == TaskStatus.PENDING
            TaskFilter.IN_PROGRESS -> task.status == TaskStatus.IN_PROGRESS
            TaskFilter.COMPLETED -> task.status == TaskStatus.COMPLETED
            TaskFilter.SUBJECT_MOBILE -> task.subject == mobileSubject
        }
        val matchesQuery = query.isBlank() ||
            task.title.contains(query.trim(), ignoreCase = true)
        matchesFilter && matchesQuery
    }

    val screenPadding = dimensionResource(R.dimen.screen_padding)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Barra de búsqueda
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = screenPadding, vertical = dimensionResource(R.dimen.spacing_small)),
            placeholder = { Text(stringResource(R.string.tasks_search_hint)) },
            leadingIcon = {
                Icon(painterResource(R.drawable.ic_search), contentDescription = null)
            },
            singleLine = true,
            shape = RoundedCornerShape(dimensionResource(R.dimen.button_corner))
        )

        // Chips de filtro
        LazyRow(
            contentPadding = PaddingValues(horizontal = screenPadding),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
        ) {
            items(TaskFilter.entries) { option ->
                FilterChip(
                    selected = filter == option,
                    onClick = { filter = option },
                    label = { Text(stringResource(option.label)) }
                )
            }
        }

        if (visibleTasks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.tasks_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(screenPadding),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))
            ) {
                items(visibleTasks, key = { it.id }) { task ->
                    SwipeableTaskItem(
                        task = task,
                        onToggle = { onToggleTask(task) },
                        onDelete = {
                            onDeleteTask(task)
                            scope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = deletedMessage,
                                    actionLabel = undoLabel,
                                    duration = SnackbarDuration.Short
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    onUndoDelete()
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.tasks_delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    ) {
        TaskCard(task = task, onToggle = onToggle)
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: () -> Unit) {
    val completed = task.status == TaskStatus.COMPLETED
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = completed, onCheckedChange = { onToggle() })
            Column {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (completed) TextDecoration.LineThrough else null,
                    color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.task_item_info,
                        task.subject,
                        task.dueDate
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
