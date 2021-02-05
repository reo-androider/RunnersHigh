package com.reo.running.runnershigh

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(val list: List<Int>,var index: Int) : RecyclerView.Adapter<MyViewHolder>() {

    private lateinit var listener: OnCourseListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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

