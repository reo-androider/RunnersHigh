package com.reo.running.runnershigh

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.formatter.YAxisValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler

class DistanceUnit : ValueFormatter, YAxisValueFormatter {
    override fun getFormattedValue(value: Float, yAxis: YAxis?): String {
        return value.toInt().toString() + "m"
    }

    override fun getFormattedValue(
        value: Float,
        entry: Entry?,
        dataSetIndex: Int,
        viewPortHandler: ViewPortHandler?
    ): String {
        TODO("Not yet implemented")
    }
}