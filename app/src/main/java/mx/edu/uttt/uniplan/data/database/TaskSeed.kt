package mx.edu.uttt.uniplan.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import mx.edu.uttt.uniplan.R
import mx.edu.uttt.uniplan.domain.model.TaskStatus

private data class SeedTask(
    val title: Int,
    val subject: Int,
    val dueDate: Int,
    val status: TaskStatus,
    val dueToday: Boolean = false
)

private val seedTasks = listOf(
    SeedTask(R.string.task_prototype, R.string.subject_mobile, R.string.date_today_night, TaskStatus.IN_PROGRESS, dueToday = true),
    SeedTask(R.string.task_linked_lists, R.string.subject_data_structures, R.string.date_today_afternoon, TaskStatus.PENDING, dueToday = true),
    SeedTask(R.string.task_calculus_set, R.string.subject_calculus, R.string.date_today_evening, TaskStatus.PENDING, dueToday = true),
    SeedTask(R.string.task_simple_past, R.string.subject_english, R.string.date_tomorrow, TaskStatus.PENDING),
    SeedTask(R.string.task_compose, R.string.subject_mobile, R.string.date_friday, TaskStatus.IN_PROGRESS),
    SeedTask(R.string.task_ethics, R.string.subject_ethics, R.string.date_done_ethics, TaskStatus.COMPLETED),
    SeedTask(R.string.task_web_report, R.string.subject_web, R.string.date_done_web, TaskStatus.COMPLETED)
)

fun insertSeedTasks(context: Context, db: SupportSQLiteDatabase) {
    seedTasks.forEach { seed ->
        val values = ContentValues().apply {
            put("title", context.getString(seed.title))
            put("subject", context.getString(seed.subject))
            put("dueDate", context.getString(seed.dueDate))
            put("status", seed.status.name)
            put("dueToday", if (seed.dueToday) 1 else 0)
        }
        db.insert("task", SQLiteDatabase.CONFLICT_IGNORE, values)
    }
}
