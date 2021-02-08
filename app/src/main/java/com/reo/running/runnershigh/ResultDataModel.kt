package com.reo.running.runnershigh

import android.graphics.Bitmap
import androidx.room.PrimaryKey
import io.realm.RealmObject

open class ResultDataModel (
    @PrimaryKey
    var memo: String = "",
    var feedBack: String = "",
    var colorId:String = "",
//    var bitmap: Bitmap? = null
) : RealmObject()