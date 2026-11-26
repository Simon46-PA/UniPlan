package mx.edu.uttt.uniplan.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.ClassSession
import mx.edu.uttt.uniplan.domain.model.Task
import mx.edu.uttt.uniplan.domain.model.TaskStatus

/** Pantalla 2. Inicio: resumen del día. */
@Composable
fun HomeScreen(
    tasks: List<Task>,
    todayClasses: List<ClassSession>,
    onToggleTask: (Task) -> Unit,
    onSeeAllTasks: () -> Unit,
    onSeeAllClasses: () -> Unit,
    contentPadding: PaddingValues
) {
    val spacing = dimensionResource(R.dimen.spacing_medium)
    val roomTbd = stringResource(R.string.room_tbd)
    val todayTasks = tasks.filter { it.dueToday }
    val completed = tasks.count { it.status == TaskStatus.COMPLETED }
    val progress = if (tasks.isEmpty()) 0f else completed.toFloat() / tasks.size

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = dimensionResource(R.dimen.screen_padding),
            end = dimensionResource(R.dimen.screen_padding),
            top = contentPadding.calculateTopPadding() + spacing,
            bottom = contentPadding.calculateBottomPadding() + spacing
        ),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        // Tarjeta 1: Para hoy
        item {
            SummaryCard {
                SectionHeader(
                    title = stringResource(R.string.home_today_title),
                    onSeeAll = onSeeAllTasks
                )
                if (todayTasks.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_no_tasks_today),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                todayTasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.status == TaskStatus.COMPLETED,
                            onCheckedChange = { onToggleTask(task) }
                        )
                        Column {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge
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
        }

        // Tarjeta 2: Clases de hoy
        item {
            SummaryCard {
                SectionHeader(
                    title = stringResource(R.string.home_classes_title),
                    onSeeAll = onSeeAllClasses
                )
                if (todayClasses.isEmpty()) {
                    Text(
                        text = stringResource(R.string.home_no_classes_today),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                todayClasses.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_school),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.padding(start = spacing)) {
                            Text(
                                text = item.subjectName,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = stringResource(
                                    R.string.class_item_info,
                                    item.startTime,
                                    item.endTime,
                                    item.room.ifBlank { roomTbd }
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Tarjeta 3: Progreso semanal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(spacing)) {
                    Text(
                        text = stringResource(R.string.home_progress_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.home_progress_value, (progress * 100).toInt()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.spacing_medium))) {
            content()
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        TextButton(onClick = onSeeAll) {
            Text(stringResource(R.string.home_see_all))
        }
    }
}
