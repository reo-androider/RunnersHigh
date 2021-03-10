package com.reo.running.runnershigh

import android.util.Log
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import kotlin.math.ceil

class DistanceUnit : YAxisValueFormatter {
    override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
        return (ceil(value * 1000f) / 1000f).toString() + "km"
    }
}