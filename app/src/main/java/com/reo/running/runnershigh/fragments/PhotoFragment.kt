package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reo.running.runnershigh.PhotoListAdapter
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.Record2
import com.reo.running.runnershigh.databinding.FragmentPhotoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoFragment : Fragment() {
    private lateinit var binding:FragmentPhotoBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            lifecycleScope.launch(Dispatchers.IO) {
                val readDao = MyApplication.db.recordDao2()
                val runData = readDao.getAll2()
                if (runData.isNotEmpty()) {
                    val lastId = runData.last().id - 1
                    for (i in lastId downTo 0) {
                        val record2 = Record2(
                                runData[i].id,
                                runData[i].bitmap,
                                runData[i].time,
                                runData[i].distance,
                                runData[i].calorie,
                                runData[i].runData,
                                runData[i].colorId,
                                runData[i].revaluationMark,
                                runData[i].memo
                        )
                    }
                    withContext(Dispatchers.Main) {
                        mainRecyclerView.adapter = PhotoListAdapter(runData)
                        mainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                        returnButton.setOnClickListener {
                            findNavController().navigate(R.id.action_fragmentPhoto_to_navi_setting)
                        }
                    }
                }
            }
        }
    }
}