package com.reo.running.runnershigh.fragments

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
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
import com.google.android.material.snackbar.Snackbar
import com.reo.running.runnershigh.MainActivity
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*
import kotlin.concurrent.timer

class FragmentRun : Fragment() {

    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var stdLocation: Location? = null
    var totalDistance = 0
    var gpsCount = 0
    var results = FloatArray(1)

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
                        //start timer!! *stopWatch is id of the chronometer.
                        stopWatch.start()
                        stopWatch.base = SystemClock.elapsedRealtime()
                        //display mapView
                        mapView.visibility = View.VISIBLE

                        //display pauseButton
                        pauseButton.visibility = View.VISIBLE
                        pauseText.visibility = View.VISIBLE

                        //display finishButton
                        finishButton.visibility = View.VISIBLE
                        finishText.visibility = View.VISIBLE

                        //display timer UI
                        timerScreen.visibility = View.VISIBLE

                        //process when restartButton is pushed
                        restartButton.setOnClickListener {
                            //start timer!!
                            stopWatch.start()
                            //display pauseButton
                            pauseText.visibility = View.VISIBLE
                            pauseButton.visibility = View.VISIBLE
                            //hide restartButton
                            restartText.visibility = View.INVISIBLE
                            restartButton.visibility = View.INVISIBLE
                        }

                        //process when pauseButton is pushed
                        pauseButton.setOnClickListener {
                            //stop chronometer
                            stopWatch.stop()
                            //hide pauseButton
                            pauseText.visibility = View.INVISIBLE
                            pauseButton.visibility = View.INVISIBLE
                            //redisplay restartButton
                            restartText.visibility = View.VISIBLE
                            restartButton.visibility = View.VISIBLE
                        }

                        finishButton.setOnLongClickListener {
                            val snack = Snackbar.make(view, "長押し…",Snackbar.LENGTH_LONG)
                            snack.view.setBackgroundColor(Color.parseColor("#00E5FF"))
                            snack.setTextColor(Color.parseColor("#212121"))
                            snack.show()
                            // result画面へ！！！
                            true
                        }
                    }
                }
            }
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
//
//        fun animation0() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 1000
//            binding.countNum0.startAnimation(scale)
//        }
//
//        fun animation1() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 1000
//            binding.countNum1.startAnimation(scale)
//        }
//
//        fun animation2() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 500
//            binding.countNum2.startAnimation(scale)
//        }
//
//        fun animation3() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 500
//            binding.countNum3.startAnimation(scale)
//        }
//
//        fun animation4() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 500
//            binding.countNum4.startAnimation(scale)
//        }
//
//        fun animation5() {
//
//            val scale = ScaleAnimation(
//                0f,
//                100f,
//                0f,
//                100f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f,
//                Animation.RELATIVE_TO_SELF,
//                0.5f
//            )
//            scale.duration = 500
//            binding.countNum5.startAnimation(scale)
//        }

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
