package com.reo.running.runnershigh.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.databinding.FragmentPhotoBinding

class FragmentPhoto : Fragment() {
    private lateinit var binding:FragmentPhotoBinding
    private val readDao = MyApplication.db.recordDao2()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPhotoBinding.inflate(layoutInflater,container,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            val photo = listOf<Bitmap>(

            )
        }
    }
}