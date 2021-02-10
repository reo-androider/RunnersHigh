package com.reo.running.runnershigh.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.DropBoxManager
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.Fragment2Binding
import java.security.KeyStore

class Fragment2 : Fragment() {
    private lateinit var binding: Fragment2Binding
    private lateinit var mLineChar:LineChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = Fragment2Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        //表示用サンプルデータの作成//
//        val x = listOf<Float>(1f, 2f, 3f, 5f, 8f, 13f, 21f, 34f)//X軸データ
//        val y = x.map{it*it}//Y軸データ（X軸の2乗）
//
//        //①Entryにデータ格納
//        var entryList = mutableListOf<Entry>()//1本目の線
//        for(i in x.indices){
//            entryList.add(
//                Entry(x[i], y[i])
//            )
//        }
//
//        //LineDataSetのList
//        val lineDataSets = mutableListOf<ILine>()
//        //②DataSetにデータ格納
//        val lineDataSet = LineDataSet(entryList, "square")
//        //③DataSetにフォーマット指定(3章で詳説)
//        lineDataSet.color = Color.BLUE
//        //リストに格納
//        lineDataSets.add(lineDataSet)
//
//        //④LineDataにLineDataSet格納
//        val lineData = LineData()
//        //⑤LineChartにLineData格納
////        lineChart = findViewById(R.id.lineChartExample)
//        binding.lineChart.data = lineData
////        lineChart.data = lineData
//        //⑥Chartのフォーマット指定(3章で詳説)
//        //X軸の設定
//        binding.lineChart.xAxis.apply {
//            isEnabled = true
//            textColor = Color.BLACK
//        }
//        //⑦linechart更新
//        binding.lineChart.invalidate()
        val entries:ArrayList<Entry> = ArrayList()
        entries.add(Entry(4f,10))

        val dataset = LineDataSet(entries,"lineChart")
        val x = Array<String>(3) {"kotlin"}

        val data = LineData(x,dataset)
        dataset.setColors(ColorTemplate.COLORFUL_COLORS)

        binding.lineChart.let {
            it.data = data
            it.animateY(5000)
        }
    }
}
