package com.reo.running.runnershigh.fragments

import io.realm.Realm
import io.realm.RealmConfiguration

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config = RealmConfiguration.Builder().build()

        Realm.setDefaultConfiguration(config)
    }
}