package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.ceil

class FragmentRun : Fragment() {

    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var stdLocation: Location? = null
    var totalDistance = 0.0
    var results = FloatArray(1)
    val zoomValue = 21.0f
    var gpsAdjust = 10
    var weight = 57.0
    var kmAmount: Double = 0.0
    var calorieAmount:  Int = 0
    var recordStop = true
    private var stopTime: Long = 0L
    private val recordDao = MyApplication.db.recordDao()
    var marker: Marker? = null
    private var runStart = false
//    val scaleAnimation = ScaleAnimation(
//        1f,
//        0.7f,
//        1f,
//        0.7f,
//        Animation.RELATIVE_TO_SELF,
//        0.5f,
//        Animation.RELATIVE_TO_SELF,
//        0.5f
//    )
//
//    val scaleDisplay = ScaleAnimation(
//        1f,
//        0f,
//        0f,
//        1f,
//        Animation.RELATIVE_TO_SELF,
//        0f,
//        Animation.RELATIVE_TO_SELF,
//        0f
//    )

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = Fragment1Binding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        binding.mapView.onCreate(savedInstanceState)
        context?.run {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }

        val locationRequest = LocationRequest().apply {
            interval = 1
            fastestInterval = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult?.lastLocation ?: return

                Log.d("checkLatLng", "${LatLng(lastLocation.latitude, lastLocation.longitude)}")

                binding.mapView.getMapAsync {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), zoomValue))

                    if (recordStop == true) {

                        stdLocation?.let {
                            Location.distanceBetween(
                                it.latitude,
                                it.longitude,
                                lastLocation.latitude,
                                lastLocation.longitude,
                                results
                            )

                            // GPS Adjustment
                            if (results[0] < 5) {
                                totalDistance += results[0]
                            }

                            Log.d("results", "${results[0]}")
                        }

                        Log.d("stdLocation", "$stdLocation")
                        Log.d("totalDistance", "$totalDistance")

                        stdLocation = lastLocation

                        kmAmount = kmConvert(totalDistance)
                        calorieAmount = calorieConvert(totalDistance, weight)
                        binding.distance.text = "$kmAmount"
                        binding.calorieNum.text = "$calorieAmount"

                    }

                    if (gpsAdjust < 10) {

                        Log.d("gpsCount", "$gpsAdjust")

                        totalDistance = 0.0

                    } else {

                        if (gpsAdjust == 10) {
                            binding.gpsSearch.visibility = View.GONE
                            binding.cardObjective.visibility = View.GONE
                            binding.startNav.visibility = View.VISIBLE
                            binding.startNav2.visibility = View.VISIBLE

                            binding.startText.visibility = View.VISIBLE
                            binding.centerCircle.visibility = View.VISIBLE

                            val alphaAnimation = AlphaAnimation(0f,1f)
                            alphaAnimation.duration = 1500

                            val alphaAnimation2 = AlphaAnimation(0f,1f)
                            alphaAnimation2.duration = 1500

                            binding.startNav.startAnimation(alphaAnimation)
                            binding.startNav2.startAnimation(alphaAnimation)

                            binding.centerCircle.startAnimation(alphaAnimation2)
                            binding.startText.startAnimation(alphaAnimation2)

                    }

                        marker?.remove()
                        marker = it.addMarker(
                            MarkerOptions().position(LatLng(lastLocation.latitude, lastLocation.longitude)))

                        marker?.showInfoWindow()
                    }

                    it.uiSettings.isScrollGesturesEnabled = true
                    it.uiSettings.isZoomGesturesEnabled = true
                    it.uiSettings.isMapToolbarEnabled = true
                    it.uiSettings.isCompassEnabled = true

                    gpsAdjust++
                    Log.d("gsp","$gpsAdjust")
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
                }
            }
        }

        // 位置情報を更新
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

            binding.centerCircle.setOnClickListener {
                binding.run {
                    mapView.visibility = View.GONE
                    startText.visibility = View.GONE
                    centerCircle.visibility = View.GONE
                    startNav.visibility = View.GONE
                    startNav2.visibility = View.GONE

                    (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            Log.d("withContext", "withContext")
                            // TODO アニメーションが起動しない
                            listOf(
                                countNum3,
                                countNum2,
                                countNum1,
                            ).map {
                                animationCount(it)
                                delay(500)
                            }
                        }

                        GlobalScope.launch(Dispatchers.Main) {
                            runStart = true

                            startNav.visibility = View.GONE
                            startNav2.visibility = View.GONE
                            stopWatch.base = SystemClock.elapsedRealtime()
                            stopWatch.start()

                            mapView.visibility = View.VISIBLE
                            pauseImage.visibility = View.VISIBLE
                            pauseButton.visibility = View.VISIBLE
                            timerScreen.visibility = View.VISIBLE


                            restartButton.setOnClickListener {
                                stopWatch.base = SystemClock.elapsedRealtime() - stopTime
                                stopWatch.start()
                                recordStop = true
                                finishButton.visibility = View.GONE
                                finishImage.visibility = View.GONE

                                GlobalScope.launch(Dispatchers.Main) {
                                    val scaleImage2 = ScaleAnimation(
                                        1f,
                                        0.5f,
                                        1f,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    val scaleButton2 = ScaleAnimation(
                                        1f,
                                        0.5f,
                                        1f,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    val scaleUp = ScaleAnimation(
                                        0.1f,
                                        1f,
                                        0.1f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )
                                    scaleImage2.duration = 500
                                    scaleImage2.fillAfter = true
                                    scaleButton2.duration = 500
                                    scaleButton2.fillAfter = true
                                    restartImage.startAnimation(scaleImage2)
                                    restartButton.startAnimation(scaleButton2)
                                    delay(300)
                                    restartImage.clearAnimation()
                                    restartButton.clearAnimation()
                                    pauseImage.visibility = View.VISIBLE
                                    pauseButton.visibility = View.VISIBLE
                                    restartImage.visibility = View.GONE
                                    restartButton.visibility = View.GONE
                                }
                            }

                            pauseButton.setOnClickListener {
                                recordStop = false
                                stopTime = SystemClock.elapsedRealtime() - stopWatch.base
                                stopWatch.stop()

                                GlobalScope.launch(Dispatchers.Main) {
                                    val scaleImage = ScaleAnimation(
                                        1f,
                                        0.6f,
                                        1f,
                                        0.6f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    val scaleButton = ScaleAnimation(
                                        1f,
                                        0.6f,
                                        1f,
                                        0.6f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )
                                    scaleImage.duration = 300
                                    scaleImage.fillAfter = true
                                    scaleButton.duration = 300
                                    scaleButton.fillAfter = true

                                    pauseImage.startAnimation(scaleImage)
                                    pauseButton.startAnimation(scaleButton)
                                    delay(500)
                                    pauseImage.clearAnimation()
                                    pauseButton.clearAnimation()
                                    pauseImage.visibility = View.GONE
                                    pauseButton.visibility = View.INVISIBLE
                                    val scaleRestartImage = ScaleAnimation(
                                        0.6f,
                                        1f,
                                        0.6f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    val scaleRestartButton = ScaleAnimation(
                                        0.6f,
                                        1f,
                                        0.6f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )
                                    val scaleFinishImage = ScaleAnimation(
                                        0.6f,
                                        1f,
                                        0.6f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    val scaleFinishButton = ScaleAnimation(
                                        0.6f,
                                        1f,
                                        0.6f,
                                        1f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                    )

                                    scaleRestartImage.let {
                                        it.duration = 300
                                        it.fillAfter = true
                                    }
                                    scaleRestartButton.let {
                                        it.duration = 300
                                        it.fillAfter = true
                                    }
                                    scaleFinishImage.let {
                                        it.duration = 300
                                        it.fillAfter = true
                                    }
                                    scaleFinishButton.let {
                                        it.duration = 300
                                        it.fillAfter = true
                                    }

                                    finishImage.visibility = View.VISIBLE
                                    finishButton.visibility = View.VISIBLE
                                    restartImage.visibility = View.VISIBLE
                                    restartButton.visibility = View.VISIBLE

                                    restartImage.startAnimation(scaleRestartImage)
                                    restartButton.startAnimation(scaleRestartButton)
                                    finishImage.startAnimation(scaleFinishImage)
                                    finishButton.startAnimation(scaleFinishButton)
                                    delay(300)
                                    restartImage.clearAnimation()
                                    restartButton.clearAnimation()
                                    finishImage.clearAnimation()
                                    finishButton.clearAnimation()
                                }
                            }

                            finishButton.setOnClickListener {
                                Log.d("test", stopWatch.text.toString())

                                val builder = AlertDialog.Builder(requireContext())
                                builder
                                    .setCancelable(false)
                                    .setMessage("ランニングを終了しますか？")
                                    .setPositiveButton("YES",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                var record = Record(
                                                    0,
                                                    stopWatch.text.toString(),
                                                    kmAmount,
                                                    calorieAmount,
                                                    getRunDate()
                                                )
                                                recordDao.insertRecord(record)
                                                Log.d("room", "${recordDao.getAll()}")
                                                withContext(Dispatchers.Main) {
                                                    findNavController().navigate(R.id.action_navi_run_to_fragmentResult)
                                                }
                                            }
                                        })
                                    .setNegativeButton("CANCEL",
                                        DialogInterface.OnClickListener { dialog, which ->
                                            dialog.dismiss()
                                        })
                                builder.show()
//                                lifecycleScope.launch(Dispatchers.IO) {
//                                    Log.d("read","${recordDao.getAll()}")
//                                    val record = Record(0,timeAmount kmAmount, ,calorieAmount,SystemClock.elapsedRealtime())
//                                    recordDao.insertRecord(record)
//
//                                    withContext(Dispatchers.Main) {
//
//                                    }
//                             }
                            }
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

    private fun animationCount(view: View) {
        view.startAnimation(ScaleAnimation(
            0f,
            400f,
            0f,
            400f,
            Animation.RELATIVE_TO_SELF,
            // 0に近づけると右に移動される
            0.255f,
            Animation.RELATIVE_TO_SELF,
            0.55f
        ).apply { duration = 500 })
    }

    private fun kmConvert(distance: Double): Double {
        return ceil(distance) / 1000
    }

    private fun calorieConvert(distance: Double, weight: Double): Int {
            return (ceil(distance) * weight / 1000).toInt()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getRunDate(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        return formatted
    }

//    private fun pushDown(view:View) {
//        val scaleAnimation = ScaleAnimation(
//            1f,
//            0.7f,
//            1f,
//            0.7f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f
//        ).let {
//
//            it.duration = 300
//            it.fillAfter = true
//        }
//
//        view.startAnimation(scaleAnimation)
//        view.clearAnimation()
//    }

}
