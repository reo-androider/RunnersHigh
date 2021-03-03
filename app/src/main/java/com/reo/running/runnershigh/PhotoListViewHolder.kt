package com.reo.running.runnershigh

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PhotoListViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView) {
    val runPhoto: ImageView = itemView.findViewById(R.id.runPhoto)
    val runDate : TextView = itemView.findViewById(R.id.run_date)
    val runTime :TextView = itemView.findViewById(R.id.timeData)
    val runDistance:TextView = itemView.findViewById(R.id.distanceData)
    val runCalorie:TextView = itemView.findViewById(R.id.calorieData)
    val runMemo:TextView = itemView.findViewById(R.id.memo)
    val runFeedBack:ImageView = itemView.findViewById(R.id.feed_back_data)
}