package com.reo.running.runnershigh

import android.app.Application
import androidx.room.Room

class MyApplication: Application() {

    companion object {
        lateinit var db: RecordDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            this,
            RecordDatabase::class.java,
            "database-name"
        ).build()
    }
}