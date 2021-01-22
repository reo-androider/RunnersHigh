package com.reo.running.runnershigh.fragments

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reo.running.runnershigh.MainActivity
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*
import kotlin.concurrent.timer

class Fragment1 : Fragment() {

    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var stdLocation: Location? = null
    var totalDistance = 0
    var gpsCount = 0
    var count = 0
    var count10 = 0
    var countMinutes = 0
    var countMinutes10 = 0
    var countHour = 0
    var results = FloatArray(1)
    var stopWatch = true
    var againWatch = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = Fragment1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        context?.run {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        //どのような取得方法を要求
        val locationRequest = LocationRequest().apply {
            //精度重視と省電力重視を両立するため2種類の更新間隔を指定
            interval = 1                                           //最遅の更新間隔（ただし正確ではない）
            fastestInterval = 1                                    //最短の更新間隔
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY      //精度重視
        }

        //コールバック
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult?.lastLocation ?: return
                binding.mapView.getMapAsync {

                    // zoom-in
                    val zoomValue = 25.0f
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            ), zoomValue
                        )
                    )
                    //it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),20f))
                    // 平行移動可能に
                    it.uiSettings.isScrollGesturesEnabled = true
                    // 縮尺変更
                    it.uiSettings.isZoomGesturesEnabled = true
                    // Marker押すとでてくるよ
                    it.uiSettings.isMapToolbarEnabled = true
                    //コンパス
                    it.uiSettings.isCompassEnabled = true
                    context?.run {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            it.isMyLocationEnabled = true
                        }

                    }
                    stdLocation?.let {
                        Location.distanceBetween(
                            it.latitude, it.longitude,
                            lastLocation.latitude, lastLocation.longitude, results
                        )

                        if (results[0] < 5) {
                            totalDistance += results[0].toInt()
                            println(totalDistance)
                        }

                        if (gpsCount < 10) {
                            totalDistance = 0
                            gpsCount++
                            println("count")
                        }
                    }
                }
            }
        }

        //位置情報を更新
        context?.run {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

        binding.startButton.setOnClickListener {
//
//            binding.start.visibility = View.GONE
//            binding.mapView.visibility = View.INVISIBLE
//            (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
//
//            GlobalScope.launch {
//                animation5()
//                delay(1000)
//                animation4()
//                delay(1000)
//                animation3()
//                delay(1000)
//                animation2()
//                delay(1000)
//                animation1()
//                delay(1000)
//                animation0()
//                delay(1000)
//            }

            binding.run {
                startText.visibility = View.GONE
                startButton.visibility = View.GONE
                mapView.visibility = View.INVISIBLE
                (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        listOf(
                            countNum5,
                            countNum4,
                            countNum3,
                            countNum2,
                            countNum1,
                            countNum0
                        ).map {
                            animationCount(it)
                            delay(1000)
                        }
                    }
                    withContext(Dispatchers.Main) {
                        mapView.visibility = View.VISIBLE
                        pauseButton.visibility = View.VISIBLE
                        pauseText.visibility = View.VISIBLE
                        finishButton.visibility = View.VISIBLE
                        finishText.visibility = View.VISIBLE
                        timerScreen.visibility = View.VISIBLE


                        // TODO ストップウォッチの時間を止めた後に再開されない（具体的には、fabが押せない）
                        while (true) {
                            while (againWatch) {
                                // RESTART
                                restartButton.setOnClickListener {
                                    stopWatch = true
                                    pauseText.visibility = View.VISIBLE
                                    pauseButton.visibility = View.VISIBLE
                                    restartText.visibility = View.INVISIBLE
                                    restartButton.visibility = View.INVISIBLE
                                }
                                while (stopWatch) {
                                    // PAUSE
                                    pauseButton.setOnClickListener {
                                        stopWatch = false
                                        pauseText.visibility = View.INVISIBLE
                                        pauseButton.visibility = View.INVISIBLE
                                        restartText.visibility = View.VISIBLE
                                        restartButton.visibility = View.VISIBLE
                                    }

                                    //FINISH
                                    finishButton.setOnClickListener {
                                    }

                                    delay(1000)
                                    count++

                                    if (count > 9) {
                                        count10++
                                        count = 0
                                    }

                                    if (count10 > 5) {
                                        countMinutes++
                                        count10 = 0
                                    }

                                    if (countMinutes > 9) {
                                        countMinutes10++
                                        countMinutes = 0
                                    }

                                    if (countMinutes10 > 5) {
                                        countHour++
                                        countMinutes10 = 0
                                    }
                                    binding.timeCnt.setText("$countHour:$countMinutes10$countMinutes:$count10$count")
                                    break
                                }
                                break
                            }
                        }
                    }
                }
            }
            println("hey!")
        }
    }
        override fun onStart() {
            super.onStart()
            binding.mapView.onStart()
        }

        override fun onResume() {
            super.onResume()
            binding.mapView.onResume()
        }

        override fun onPause() {
            super.onPause()
            binding.mapView.onPause()
        }
/*
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
    */

        fun animation0() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 1000
            binding.countNum0.startAnimation(scale)
        }

        fun animation1() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 1000
            binding.countNum1.startAnimation(scale)
        }

        fun animation2() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 500
            binding.countNum2.startAnimation(scale)
        }

        fun animation3() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 500
            binding.countNum3.startAnimation(scale)
        }

        fun animation4() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 500
            binding.countNum4.startAnimation(scale)
        }

        fun animation5() {

            val scale = ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            scale.duration = 500
            binding.countNum5.startAnimation(scale)
        }

        private fun animationCount(view: View) {
            view.startAnimation(ScaleAnimation(
                0f,
                100f,
                0f,
                100f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply { duration = 1000 })
        }
}
