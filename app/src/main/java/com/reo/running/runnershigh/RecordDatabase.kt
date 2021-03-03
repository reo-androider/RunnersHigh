package com.reo.running.runnershigh

import android.graphics.Bitmap
import androidx.room.*

@Database(entities = arrayOf(
    JustRunData::class,
    RunResult::class) ,version = 1)
@TypeConverters(BitmapForRoom::class)
abstract class RunRecordDatabase : RoomDatabase() {
    abstract fun justRunDao(): JustRunDataDao
    abstract fun runResultDao(): RunResultDao
}

@Entity
data class JustRunData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val time: String,
    val distance: Double,
    val calorie: Int,
    val runDate: String,
    val bitmap: Bitmap?,
    val takenPhoto: Boolean
)

@Entity
data class RunResult(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val bitmap: Bitmap?,
    val time: String,
    val distance: Double,
    val calorie: Int,
    val runData: String,
    val colorId: String,
    val revaluationMark: String,
    val memo: String
    )

@Dao
interface JustRunDataDao {
    @Query("Select * From justrundata")
    suspend fun getAll(): List<JustRunData>

    @Query("select * From justrundata Where id = :id")
    suspend fun getRecord(id: Int): JustRunData

    @Insert
    suspend fun insertRecord(record: JustRunData)

    @Update
    suspend fun updateRecord(record: JustRunData)

    @Delete
    suspend fun deleteRecord(record: JustRunData)

}


@Dao
interface RunResultDao {
    @Query("Select * From runresult")
    suspend fun getAll(): List<RunResult>

    @Query("select * From runresult Where id = :id")
    suspend fun getRecord(id: Int): RunResult

    @Insert
    suspend fun insertRecord(record: RunResult)

    @Update
    suspend fun updateRecord(record: RunResult)

    @Delete
    suspend fun deleteRecord(recordList: List<RunResult>)
}
