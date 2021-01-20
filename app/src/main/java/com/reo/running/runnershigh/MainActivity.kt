package com.reo.running.runnershigh

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.reo.running.runnershigh.databinding.ActivityMainBinding
import java.util.zip.Inflater


class MainActivity : AppCompatActivity() {

    lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        val navController = findNavController(R.id.nav_host_fragment)
        setupWithNavController(binding.bottomNavigation,navController)

    }
}