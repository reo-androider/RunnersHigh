package com.reo.running.runnershigh

import android.graphics.Bitmap
import androidx.room.*

@Database(entities = arrayOf(
    Record::class,
    Record2::class) ,version = 1)
@TypeConverters(BitmapConverter::class)
abstract class RecordDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun recordDao2(): RecordDao2
}

@Entity
data class Record(
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
data class Record2(
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


@Dao
interface RecordDao2 {
    @Query("Select * From record2")
    fun getAll2(): List<Record2>

    @Query("select * From record2 Where id = :id")
    suspend fun getRecord2(id: Int): Record2

    @Insert
    suspend fun insertRecord2(record: Record2)

    @Update
    suspend fun updateRecord2(record: Record2)

    @Delete
    suspend fun deleteRecord2(recordList: List<Record2>)
}
