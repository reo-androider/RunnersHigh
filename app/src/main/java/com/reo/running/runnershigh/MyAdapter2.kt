package com.reo.running.runnershigh

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
        holder.run {
            runPhoto.run {
                setImageBitmap(list[position].bitmap)
                if (list[position].colorId.isNotEmpty()) {
                    setBackgroundColor(Color.parseColor(list[position].colorId))
                }
            }
            runDate.run {
                setText(list[position].runData)
            }
            runTime.run {
                setText(list[position].time)
            }
            runDistance.run {
                text = "${list[position].distance}km"
            }
            runCalorie.run {
                text = "${list[position].calorie}kcal"
            }
            runMemo.text = "${list[position].memo}"

            runFeedBack.run {
                when (list[position].revaluationMark) {
                    "perfect" -> {
                        setImageResource(R.drawable.ic_perfect)
                        visibility = View.VISIBLE
                    }
                    "good" -> {
                        setImageResource(R.drawable.ic_good)
                        visibility = View.VISIBLE
                    }
                    "soso" -> {
                        setImageResource(R.drawable.ic_soso)
                        visibility = View.VISIBLE
                    }
                    "bad" -> {
                        setImageResource(R.drawable.ic_bad)
                        visibility = View.VISIBLE
                    }
                    "tooBad" -> {
                        setImageResource(R.drawable.ic_sick)
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int = list.size
}

