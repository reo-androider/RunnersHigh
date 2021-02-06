package com.reo.running.runnershigh

import androidx.room.PrimaryKey
import io.realm.RealmObject

open class Memo (
    @PrimaryKey var id: Int = 0,
    var memo: String = ""
) : RealmObject()
