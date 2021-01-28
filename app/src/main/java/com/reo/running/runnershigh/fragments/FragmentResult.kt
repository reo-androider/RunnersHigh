package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.databinding.FragmentResultBinding

class FragmentResult : Fragment() {

//    private val recordDao = MyApplication.db.recordDao()

    private lateinit var binding:FragmentResultBinding
    val bundle = Bundle()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentResultBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}