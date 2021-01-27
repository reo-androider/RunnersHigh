package com.reo.running.runnershigh

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name="first_name") val todoTitle: String?,
    @ColumnInfo(name = "last_name") val lastName: String?
    ) {

}