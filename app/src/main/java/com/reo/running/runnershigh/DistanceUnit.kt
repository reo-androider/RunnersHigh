package com.reo.running.runnershigh

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.YAxisValueFormatter

class DistanceUnit : YAxisValueFormatter {
    override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
        return (value/1000).toString() + "km"
    }
}