package com.reo.running.runnershigh.fragments

import android.graphics.Color
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.databinding.FragmentGraphBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentGraph : Fragment() {
    private lateinit var binding: FragmentGraphBinding
    private var entriesTime: ArrayList<Entry> = ArrayList()
    private var entries: ArrayList<Entry> = ArrayList()
    private var entries2:ArrayList<Entry> = ArrayList()
    private var lastId = 0
    private var totalDistance = 0.0
    private var totalCalorie = 0.0
    private var totalTime = 0
    private val runDatabase = MyApplication.db.recordDao2()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentGraphBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            (activity as MainActivity).binding.bottomNavigation.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {
                val read = runDatabase.getAll2()
                if (read.isNotEmpty()) {
                    val xValue = mutableListOf<String>()
                    lastId = read.last().id - 1
                    for (i in 0..lastId) {
                        xValue.add(read[i].runData)
                    }
                    val yValue = mutableListOf<Double>()
                    for (i in 0..lastId) {
                        yValue.add((read[i].distance) + totalDistance)
                        totalDistance += read[i].distance

                    }
                    withContext(Dispatchers.Main) {
                        for (i in 0..lastId) {
                            entries.add(i, Entry(mConverter(yValue[i]), i))
                        }
                        val dataset = LineDataSet(entries, "")
                        val timeData = LineData(xValue, dataset)
                        val data = LineData(xValue, dataset)
                        dataset.setColors(ColorTemplate.COLORFUL_COLORS)
                        distanceGraph.getAxisRight().run {
                            isEnabled = false
                            setDrawLabels(false)
                            setDrawGridLines(true)
                        }
                        distanceGraph.run {
                            invalidate()
                            this.data = data
                            animateY(1)
                            axisLeft.valueFormatter = DistanceUnit()
                        }
                    }
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                totalCalorie.let {
                    val read = runDatabase.getAll2()
                    if (read.isNotEmpty()) {
                        Log.d("ROOM", "${read}")

                        val xValue2 = mutableListOf<String>()
                        lastId = read.last().id - 1
                        for (i in 0..lastId) {
                            xValue2.add(read[i].runData)
                        }

                        val yValue2 = mutableListOf<Double>()
                        for (i in 0..lastId) {
                            yValue2.add((read[i].calorie) + totalCalorie)
                            Log.d("ROOM", "${yValue2[i]}")
                            totalCalorie += read[i].calorie
                        }

                        withContext(Dispatchers.Main) {
                            for (i in 0..lastId) {
                                entries2.add(i, Entry(yValue2[i].toFloat(), i))
                            }
                            val dataset2 = LineDataSet(entries2, "")

                            val data2 = LineData(xValue2, dataset2)
                            dataset2.setColors(ColorTemplate.COLORFUL_COLORS)
                            graphCalorie.getAxisRight().run {
                                isEnabled = false
                                setDrawLabels(false)
                                setDrawGridLines(true)
                            }
                            graphCalorie.run {
                                invalidate()
                                this.data = data2
                                animateY(1)
                                axisLeft.valueFormatter = CalorieUnit()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun mConverter(km: Double): Float {
        return (km * 1000).toFloat()
    }
}

