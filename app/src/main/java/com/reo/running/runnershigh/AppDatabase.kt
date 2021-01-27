package com.reo.running.runnershigh

import androidx.room.*
import com.twitter.sdk.android.core.models.User

@Database(entities = [User::class],version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun userDao(): UserDao
}

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
//    val title: String,
//    val description: String,
//    val createAt: Long,
//    val updatedAt: Long,
//    val onComplete: Boolean
//    @ColumnInfo(name="first_name") val todoTitle: String?,
//    @ColumnInfo(name = "last_name") val lastName: String?
)

//fun Long.toDateText(): String {
//    return SimpeleDataFormat("yyy年MM月dd日",Locale.JAPANESE).
//}

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<com.reo.running.runnershigh.User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<com.reo.running.runnershigh.User>

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " + "last_name LIKE :last LIMIT 1")
    suspend fun findByName(first: String, last: String): com.reo.running.runnershigh.User

    @Insert
    suspend fun insertAll(users: Int)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

}