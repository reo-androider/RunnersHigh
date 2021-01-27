package com.reo.running.runnershigh

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Address constructor(
    @PrimaryKey(autoGenerate = true)
    val id:Long,
    val address: String,
    val name: String
    ) {

}