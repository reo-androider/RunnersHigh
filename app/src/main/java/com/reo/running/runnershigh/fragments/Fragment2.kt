package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.Fragment2Binding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.runData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Fragment2 : Fragment() {
    private lateinit var binding: Fragment2Binding
    private var totalDistance = 0
    private val runDatabase = MyApplication.db.recordDao2()
    private var entries:ArrayList<Entry> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = Fragment2Binding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {


            //日にちをインクリメント順に取れれば勝ち
            val read = runDatabase.getAll2()
            Log.d("array","${read.size}")
            Log.d("array","${read}")
            Log.d("array","${read[0].runData}")
            Log.d("array","${read[1].runData}")

            val x = mutableListOf<String>()
            var i = 0
            val lastId = read.last().id -1
            for (i in 0..lastId) {
                x.add(read[i].runData)
            }

            val y = mutableListOf<Double>()
            for (i in 0..lastId) {
                y.add(read[i].distance)
            }
            Log.d("array","$y")

            Log.d("array","${x}")
            // 点を作っている
            (read.lastOrNull()?.distance)?.toFloat()?.let { Entry(it,10) }?.let { entries.add(it) }
        }

        lifecycleScope.launch(Dispatchers.Main) {

            // 線を作っている
            val dataset = LineDataSet(entries, "")
            val x = Array<String>(1) { "" }

            //Dataそのもの
            val data = LineData(x, dataset)
            dataset.setColors(ColorTemplate.COLORFUL_COLORS)

            //設定
            val leftAxis = binding.lineChart.getAxisLeft()
            leftAxis.axisMaxValue = 100f
            leftAxis.axisMinValue = 0f
            val rightAxis = binding.lineChart.getAxisRight()
            rightAxis.isEnabled = false


            //更新
            binding.lineChart.invalidate()

            // Dataを格納する箱
            binding.lineChart.data = data
            binding.lineChart.animateY(0)

        }
    }
}

