//package com.reo.running.runnershigh
//
//import androidx.room.Database
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.RoomDatabase
//import java.util.*
//
//@Database(entities = [Task::c])
//abstract class RunDatabase : RoomDatabase {
//    abstract fun taskDao: TaskDao
//}
//
//@Entity
//data class Task(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int,
//    val title:String,
//    val description: String,
//    val createAt: Long,
//    val updateAt: Long,
//    val onComplete: Boolean
//)
//
//fun Long.toDataText(): String {
//    return SimpleDataFormat("yyyy年,MM月,dd日",Locale.JAPANESE).format(Data(this))
//}