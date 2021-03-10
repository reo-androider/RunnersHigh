package com.reo.running.runnershigh.recyclerview.paints

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.R

class PaintsAdapter(val list: List<Int>, var index: Int) : RecyclerView.Adapter<paintsViewHolder>() {

    private lateinit var listener: OnCourseListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): paintsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.paints_item_recyclerview,parent,false)
        return paintsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: paintsViewHolder, position: Int) {
        holder.course.setImageResource(list[position])

        holder.itemView.setOnClickListener {
            listener.onItemClick(list,position)
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnCourseListener {
        fun onItemClick(list: List<Int>,position: Int)
    }

    fun setOnItemClickListener(listener: OnCourseListener) {
        this.listener = listener
    }


}

