package com.reo.running.runnershigh

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart

class ViewHolderForGraph(itemView:View) : RecyclerView.ViewHolder(itemView) {
    val lineChart:LineChart = itemView.findViewById(R.id.lineChart)
    val mark:ImageView = itemView.findViewById(R.id.mark)
}