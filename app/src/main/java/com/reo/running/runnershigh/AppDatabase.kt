package com.reo.running.runnershigh

import androidx.room.*

@Database(entities = arrayOf(User::class),version = 1)
abstract class AppDatabase:RoomDatabase() {
    abstract fun userDao(): UserDao
}