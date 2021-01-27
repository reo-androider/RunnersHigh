package com.reo.running.runnershigh

import androidx.room.*

@Database(entities = arrayOf(AddressDao::class),version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun adderssDao(): AddressDao
}