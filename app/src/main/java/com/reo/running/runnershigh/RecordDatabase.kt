package com.reo.running.runnershigh

import androidx.room.*
import com.twitter.sdk.android.core.models.User
import java.text.SimpleDateFormat
import java.util.*

@Database(entities = [Record::class], version = 1)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val distance: Double,
    val time: Long,
    val calorie: Double,
    val date: Long,
    val onComplete: Boolean
)

@Dao
interface RecordDao {
    @Query("Select * From record")
    suspend fun getAll(): List<Record>

    @Query("select * From record Where id = :id")
    suspend fun getRecord(id: Int): Record

    @Insert
    fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

}