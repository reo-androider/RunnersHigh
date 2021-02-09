package com.reo.running.runnershigh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.reo.running.runnershigh.databinding.ActivityMainBinding
import com.reo.running.runnershigh.databinding.Fragment2Binding
import com.reo.running.runnershigh.fragments.Fragment2
import java.util.zip.Inflater


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding
    val fragment = Fragment2()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(binding.bottomNavigation,navController)

    }
}