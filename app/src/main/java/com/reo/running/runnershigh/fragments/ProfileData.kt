package com.reo.running.runnershigh.fragments

import android.graphics.Bitmap

data class ProfileData(
    val bitmap: Bitmap? = null,
    val firstName:String = "あなたの",
    val familyName:String = "名前",
    val objective:String = "",
    val weight:String = ""
    )