package com.reo.running.runnershigh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter internal constructor(private var rowDataList: List<RowData>) : RecyclerView.Adapter<MyAdapter.MainViewHodler>() {

    /**
     * ViewHolder作るメソッド
     * 最初しか呼ばれない。
     * ここでViewHolderのlayoutファイルをインフレーとして生成したViewHolderをRecyclerViewに返す。
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHodler {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_illustration,parent,false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHodler, position: Int) {
        val rowData = rowDataList[position]
        holder.hogeTitle.text = rowData.hogeTitle
        holder.hogeContents.text = rowData.hogeContents
    }

    override fun getItemCount(): Int {
        return rowDataList.size
    }

    class MainViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var hogeTitle: TextView = itemView.findViewById(R.id.hoge_title_text_view)
        var hogeContents: TextView = itemView.findViewById(R.id.hoge_contents_text_view)
    }


}