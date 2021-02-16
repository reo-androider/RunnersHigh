package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.reo.running.runnershigh.DistanceUnit
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.ActivityLoginBinding.inflate
import com.reo.running.runnershigh.databinding.FragmentCalorieBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class FragmentCalorie :Fragment() {
    private lateinit var binding:FragmentCalorieBinding
    private val runDatabase = MyApplication.db.recordDao2()
    private var entries:ArrayList<Entry> = ArrayList()
    private var lastId = 0
    private var totalCalorie = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentCalorieBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            lifecycleScope.launch(Dispatchers.IO) {
                totalCalorie.let {

                    val read = runDatabase.getAll2()
                    Log.d("ROOM","${read}")

                    val xValue = mutableListOf<String>()
                    lastId = read.last().id - 1
                    for (i in 0..lastId) {
                        xValue.add(read[i].runData)
                    }

                    val yValue = mutableListOf<Double>()
                    for (i in 0..lastId) {
                        yValue.add((read[i].calorie) + totalCalorie)
                        Log.d("ROOM","${yValue[i]}")
                        totalCalorie += read[i].calorie
                    }

                    withContext(Dispatchers.Main) {
                        for (i in 0..lastId) {
                            entries.add(i, Entry(yValue[i].toFloat(), i))
                        }
                        val dataset = LineDataSet(entries, "")

                        val data = LineData(xValue, dataset)
                        dataset.setColors(ColorTemplate.COLORFUL_COLORS)

                        val leftAxis = binding.lineChart.getAxisLeft().run {
                            axisMinValue = 0f
                            when {
                                it == 0.0 -> axisMaxValue = 100f
                                it > 100.0 -> axisMaxValue = 500f
                                it > 500.0 -> axisMaxValue = 1000f
                                it > 1000.0 -> axisMaxValue = 2000f
                                it > 2000.0 -> axisMaxValue = 5000f
                                it > 5000.0 -> axisMaxValue = 10000f
                                it > 10000.0 -> axisMaxValue = 10000000f
                            }
                            valueFormatter = DistanceUnit()
                        }

                        val rightAxis = binding.lineChart.getAxisRight().run {
                            isEnabled = false
                            setDrawLabels(false)
                            setDrawGridLines(true)
                        }

                        lineChart.run {
                            invalidate()
                            this.data = data
                            animateY(1)
                        }
                    }
                }
            }
        }
    }
}