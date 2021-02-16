package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.reo.running.runnershigh.DistanceUnit
import com.reo.running.runnershigh.MainActivity
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.databinding.FragmentGraphBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentGraph : Fragment() {
    private lateinit var binding: FragmentGraphBinding
    private val runDatabase = MyApplication.db.recordDao2()
    private var entries:ArrayList<Entry> = ArrayList()
    private var lastId = 0
    private var totalDistance = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentGraphBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            (activity as MainActivity).binding.bottomNavigation.visibility =
                View.VISIBLE
            lifecycleScope.launch(Dispatchers.IO) {

                val read = runDatabase.getAll2()

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
                        entries.add(i, Entry(mConverter(yValue[i]),i))
                    }
                    val dataset = LineDataSet(entries, "")

                    val data = LineData(xValue, dataset)
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS)

                    val leftAxis = binding.lineChart.getAxisLeft().run {
                        axisMinValue = 0f
                        when {
                            totalDistance == 0.0 -> axisMaxValue = 1000f
                            totalDistance > 1000.0 -> axisMaxValue = 3000f
                            totalDistance > 3000.0 -> axisMaxValue = 6000f
                            totalDistance > 6000.0 -> axisMaxValue = 12000f
                            totalDistance > 12000.0 -> axisMaxValue = 24000f
                            totalDistance > 24000.0 -> axisMaxValue = 48000f
                            totalDistance > 48000.0 -> axisMaxValue = 96000f
                            totalDistance > 96000.0 -> axisMaxValue = 200000f
                        }
                        valueFormatter = DistanceUnit()
                    }

                    val rightAxis = binding.lineChart.getAxisRight().run{
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

    private fun mConverter(km:Double): Float {
        return (km * 1000).toFloat()
    }
}

