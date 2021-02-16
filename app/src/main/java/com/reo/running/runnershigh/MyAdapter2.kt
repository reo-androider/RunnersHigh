package com.reo.running.runnershigh

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter2(val list: List<Bitmap?>) : RecyclerView.Adapter<MyViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_photo_item,parent,false)
        return MyViewHolder2(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        holder.runPhoto.setImageBitmap(list[position])
    }
    override fun getItemCount(): Int = list.size
}