package com.reo.running.runnershigh.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.ActivityLoginBinding.bind
import com.reo.running.runnershigh.databinding.ActivityLoginBinding.inflate
import com.reo.running.runnershigh.databinding.FragmentProfileBinding
import com.reo.running.runnershigh.databinding.FragmentProfileSettingBinding
import javax.sql.RowSet

class FragmentProfileSetting : Fragment() {
    private lateinit var binding:FragmentProfileSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            profileBack.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
            }


        }
    }
}