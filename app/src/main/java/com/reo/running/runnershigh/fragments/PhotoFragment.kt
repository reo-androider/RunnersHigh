package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.databinding.FragmentPhotoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoFragment : Fragment() {
    private lateinit var binding:FragmentPhotoBinding

    private val viewModel: PhotoListViewModel by viewModels {
        PhotoListViewModel.Companion.Factory(MyApplication.db.runResultDao())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
//            lifecycleScope.launch(Dispatchers.IO) {
//                val readDao = MyApplication.db.runResultDao()
//                val runData = readDao.getAll()
//                var setData = listOf<RunResult>()
//                if (runData.isNotEmpty()) {
//                    val lastId = runData.last().id - 1
//                    for (i in lastId downTo 0) {
//                        val record2 = RunResult(
//                                runData[i].id,
//                                runData[i].bitmap,
//                                runData[i].time,
//                                runData[i].distance,
//                                runData[i].calorie,
//                                runData[i].runData,
//                                runData[i].colorId,
//                                runData[i].revaluationMark,
//                                runData[i].memo
//                        )
//                        setData += record2
//                    }
//                    withContext(Dispatchers.Main) {
//                        mainRecyclerView.run {
//                            adapter = PhotoListAdapter(setData)
//                            layoutManager = LinearLayoutManager(requireContext())
//                        }
//                        returnButton.setOnClickListener {
//                            findNavController().navigate(R.id.action_fragmentPhoto_to_navi_setting)
//                        }
//                    }
            viewModel.runResultList.observe(viewLifecycleOwner) {
                mainRecyclerView.run {
                    adapter = PhotoListAdapter(it)
                    layoutManager = LinearLayoutManager(requireContext())
                    returnButton.setOnClickListener {
                        findNavController().navigate(R.id.action_fragmentPhoto_to_navi_setting)
                    }
                }
            }
        }
    }
}