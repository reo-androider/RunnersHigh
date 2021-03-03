package com.reo.running.runnershigh

import android.app.Application
import androidx.room.Room
import io.realm.Realm
import io.realm.RealmConfiguration

class MyApplication: Application() {

    companion object {
        lateinit var db: RunRecordDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(
            this,
            RunRecordDatabase::class.java,
            "database-name"
        ).build()

        Realm.init(this)
        val realmConfig = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}