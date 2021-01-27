package com.reo.running.runnershigh

import android.app.Application
import androidx.room.Room

class MyApplication: Application() {

    companion object {
        lateinit var db: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "database-name"
        ).build()
    }
}