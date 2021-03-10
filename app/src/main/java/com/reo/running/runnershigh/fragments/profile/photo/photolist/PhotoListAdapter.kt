package com.reo.running.runnershigh.fragments.profile.photo.photolist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.RunResult

class PhotoListAdapter(val list: List<RunResult>) : RecyclerView.Adapter<PhotoListViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_photo_item, parent, false)
        return PhotoListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoListViewHolder, position: Int) {
        holder.run {
            runPhoto.run {
                setImageBitmap(list[position].bitmap)
                rotation = 90f
                if (list[position].colorId.isNotEmpty()) {
                    setBackgroundColor(Color.parseColor(list[position].colorId))
                }
            }
            runDate.setText(list[position].runData)
            runTime.setText(list[position].time)
            runDistance.text = "${list[position].distance}km"
            runCalorie.text = "${list[position].calorie}kcal"
            runMemo.text = "${list[position].memo}"
            runFeedBack.run {
                when (list[position].revaluationMark) {
                    FeedBack.PERFECT.revaluationMark -> {
                        setImageResource(R.drawable.ic_perfect)
                        setColorFilter(ContextCompat.getColor(context, R.color.perfect))
                    }
                    FeedBack.GOOD.revaluationMark -> {
                        setImageResource(R.drawable.ic_good)
                        setColorFilter(ContextCompat.getColor(context, R.color.good))
                    }
                    FeedBack.SOSO.revaluationMark -> {
                        setImageResource(R.drawable.ic_soso)
                        setColorFilter(ContextCompat.getColor(context, R.color.soso))
                    }
                    FeedBack.BAD.revaluationMark -> {
                        setImageResource(R.drawable.ic_bad)
                        setColorFilter(ContextCompat.getColor(context, R.color.bad))
                    }
                    FeedBack.TOOBAD.revaluationMark -> {
                        setImageResource(R.drawable.ic_sick)
                        setColorFilter(ContextCompat.getColor(context, R.color.tooBad))
                    }
                }
                if (list[position].revaluationMark != "") visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int = list.size
}

enum class FeedBack(val revaluationMark: String) {
    PERFECT("perfect"),
    GOOD("good"),
    SOSO("soso"),
    BAD("bad"),
    TOOBAD("tooBad")
}

