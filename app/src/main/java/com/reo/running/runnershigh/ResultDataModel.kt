package com.reo.running.runnershigh

import android.graphics.Bitmap
import androidx.room.PrimaryKey
import io.realm.RealmObject
import io.realm.annotations.Required

open class ResultDataModel (
    @PrimaryKey open var id : Int = 0,
    @Required open var memo: String = "",
    open var feedBack: String = "",
    open var colorId:String = "",
//    var bitmap: Bitmap? = null
) : RealmObject()