package com.reo.running.runnershigh

import android.graphics.Bitmap
import androidx.room.*

@Database(entities = [Record::class], version = 1)
@TypeConverters(BitmapConverter::class)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

@Entity
data class Record(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val time: String,
        val distance: Double,
        val calorie: Int,
        val runDate: String,
        val bitmap: Bitmap?
)

@Dao
interface RecordDao {
    @Query("Select * From record")
    fun getAll(): List<Record>

    @Query("select * From record Where id = :id")
    suspend fun getRecord(id: Int): Record

    @Insert
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

}