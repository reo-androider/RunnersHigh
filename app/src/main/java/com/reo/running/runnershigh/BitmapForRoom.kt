package com.reo.running.runnershigh

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class BitmapForRoom {
    @TypeConverter
    fun toEncodedString(bitmap: Bitmap?): String =
        bitmap?.let { b->
            val bos = ByteArrayOutputStream().also {
                if (!b.compress(Bitmap.CompressFormat.PNG,50,it)) return ""
            }
            Base64.encodeToString(bos.toByteArray(),Base64.DEFAULT)
        } ?:""

    @TypeConverter
    fun String.toBitmap(): Bitmap? {
        return Base64.decode(this,Base64.DEFAULT).let {
            BitmapFactory.decodeByteArray(it,0,it.size)
        }
    }
}