package com.reo.running.runnershigh.fragments.graph.unit

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.formatter.YAxisValueFormatter

class CalorieUnit : YAxisValueFormatter {
    override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
        return value.toInt().toString() + "kcal"
    }
}