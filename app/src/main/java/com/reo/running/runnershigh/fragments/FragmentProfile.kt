package com.reo.running.runnershigh.fragments

import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentCalorieBinding
import com.reo.running.runnershigh.databinding.FragmentProfileBinding

class FragmentProfile : Fragment(){
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
        }
    }
}