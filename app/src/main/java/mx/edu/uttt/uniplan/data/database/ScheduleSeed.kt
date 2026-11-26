package mx.edu.uttt.uniplan.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import mx.edu.uttt.uniplan.R

private data class SeedSubject(val name: Int, val room: Int)

/** El orden importa: los ids autogenerados (1..6) los usan las clases de abajo. */
private val seedSubjects = listOf(
    SeedSubject(R.string.subject_mobile, R.string.room_lab3),
    SeedSubject(R.string.subject_data_structures, R.string.room_b12),
    SeedSubject(R.string.subject_calculus, R.string.room_tbd),
    SeedSubject(R.string.subject_english, R.string.room_tbd),
    SeedSubject(R.string.subject_ethics, R.string.room_tbd),
    SeedSubject(R.string.subject_web, R.string.room_tbd)
)

private data class SeedClass(val subjectId: Int, val day: Int, val start: String, val end: String)

private val seedClasses = listOf(
    SeedClass(1, 1, "08:00", "10:00"),
    SeedClass(1, 3, "08:00", "10:00"),
    SeedClass(2, 1, "11:00", "13:00"),
    SeedClass(2, 3, "11:00", "13:00"),
    SeedClass(3, 2, "09:00", "11:00"),
    SeedClass(3, 4, "09:00", "11:00"),
    SeedClass(4, 2, "11:00", "12:00"),
    SeedClass(4, 5, "08:00", "09:00"),
    SeedClass(5, 4, "11:00", "12:00"),
    SeedClass(6, 5, "10:00", "12:00")
)

fun insertSeedSubjects(context: Context, db: SupportSQLiteDatabase) {
    seedSubjects.forEach { seed ->
        val values = ContentValues().apply {
            put("name", context.getString(seed.name))
            put("room", context.getString(seed.room))
        }
        db.insert("subject", SQLiteDatabase.CONFLICT_IGNORE, values)
    }
}

fun insertSeedClasses(db: SupportSQLiteDatabase) {
    seedClasses.forEach { seed ->
        val values = ContentValues().apply {
            put("subjectId", seed.subjectId)
            put("dayOfWeek", seed.day)
            put("startTime", seed.start)
            put("endTime", seed.end)
        }
        db.insert("class_session", SQLiteDatabase.CONFLICT_IGNORE, values)
    }
}
