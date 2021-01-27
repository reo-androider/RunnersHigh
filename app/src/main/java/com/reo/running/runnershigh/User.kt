package com.reo.running.runnershigh

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "firstName") val firstName: String?,
    @ColumnInfo(name = "last_name") val lastName: String?,
    @Ignore val age:String?
    ) {
        /*
         @Ignore を付ける場合は、テーブルに含めるプロパティで処理化する
         コンストラクタを作成する必要があります。
         */
    constructor(id: Int,firstName: String?,lastName: String?) : this(id,firstName, lastName, null)
}