package com.reo.running.runnershigh.recyclerview.paints

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.R

class paintsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val course: ImageView = itemView.findViewById(R.id.paints)
}