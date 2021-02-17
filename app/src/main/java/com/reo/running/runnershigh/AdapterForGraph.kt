package com.reo.running.runnershigh

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart

class AdapterForGraph(val list: List<Int>) : RecyclerView.Adapter<ViewHolderForGraph>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderForGraph {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_graph_item,parent,false)
        return ViewHolderForGraph(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolderForGraph, position: Int) {
        holder.mark.setImageResource(list[position])
        if(list[position] == R.drawable.ic_time) {

        }
    }
    override fun getItemCount(): Int = list.size
}