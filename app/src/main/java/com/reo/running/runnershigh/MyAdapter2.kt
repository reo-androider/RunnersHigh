package com.reo.running.runnershigh

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter2(val list: List<Record2>) : RecyclerView.Adapter<MyViewHolder2>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_photo_item,parent,false)
        return MyViewHolder2(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        //色をセットする
        holder.runPhoto.run {
            setImageBitmap(list[position].bitmap)
            if (list[position].colorId != "") {
                setBackgroundColor(Color.parseColor(list[position].colorId))

            }        }
        holder.runDate.run {
            setText(list[position].runData)
            Log.d("color","${list[position].colorId}")
//            setTextColor(Color.parseColor(list[position].colorId))
        }
        holder.runTime.run {
            setText(list[position].time)
//            setTextColor(Color.parseColor(list[position].colorId))
        }
        holder.runDistance.run {
            text = "${list[position].distance}km"
//            setTextColor(Color.parseColor(list[position].colorId))
        }
        holder.runCalorie.run {
            text = "${list[position].calorie}kcal"
//            setTextColor(Color.parseColor(list[position].colorId))
        }
        holder.runMemo.text = "${list[position].memo}"
        when(list[position].revaluationMark) {
            "perfect" -> {
                holder.runFeedBack.setImageResource(R.drawable.ic_perfect)
                holder.runFeedBack.visibility = View.VISIBLE
            }
            "good" -> {
                holder.runFeedBack.setImageResource(R.drawable.ic_good)
                holder.runFeedBack.visibility = View.VISIBLE
            }
            "soso" -> {
                holder.runFeedBack.setImageResource(R.drawable.ic_soso)
                holder.runFeedBack.visibility = View.VISIBLE
            }
            "bad" -> {
                holder.runFeedBack.setImageResource(R.drawable.ic_bad)
                holder.runFeedBack.visibility = View.VISIBLE
            }
            "tooBad" -> {
                holder.runFeedBack.setImageResource(R.drawable.ic_sick)
                holder.runFeedBack.visibility = View.VISIBLE
            }
        }
    }
    override fun getItemCount(): Int = list.size
}

