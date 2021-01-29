package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class FragmentRun : Fragment() {

    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var stdLocation: Location? = null
    var totalDistance = 0.0
    var gpsCount = 0
    var weight = 57.0
    var kmAmount: Double = 0.0
    var calorieAmount: Double = 0.0
    var recordStop = true
    private var stopTime: Long = 0L
    private val recordDao = MyApplication.db.recordDao()
    var marker: Marker? = null
    var runStart = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        val locationRequest = LocationRequest().apply {
            interval = 1
            fastestInterval = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            var results = FloatArray(1)
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult?.lastLocation ?: return
                Log.d("checkLatLng", "${LatLng(lastLocation.latitude, lastLocation.longitude)}")
                binding.mapView.getMapAsync {
                    val zoomValue = 22.0f
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), zoomValue))


                    if (gpsCount < 8) {
                        Log.d("gpsCount", "$gpsCount")
                        totalDistance = 0.0
                        gpsCount++
                    }

                    if (recordStop == true) {

                        stdLocation?.let {
                            Location.distanceBetween(
                                it.latitude,
                                it.longitude,
                                lastLocation.latitude,
                                lastLocation.longitude,
                                results
                            )

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

                    if (gpsCount > 5) {

                        if (runStart == false) {

                            marker?.remove()
                            marker = it.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        lastLocation.latitude,
                                        lastLocation.longitude
                                    )
                                )
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            Resource.getBitmap(
                                                context,
                                                R.drawable.ic_running
                                            )
                                        )
                                    )
                                    .title("You are here!")
                            )

                        }

                        else {
                            
                            marker = it.addMarker(
                                MarkerOptions().position(
                                    LatLng(
                                        lastLocation.latitude,
                                        lastLocation.longitude
                                    )
                                )
                                    .icon(
                                        BitmapDescriptorFactory.fromBitmap(
                                            Resource.getBitmap(
                                                context,
                                                R.drawable.ic_running
                                            )
                                        )
                                    )
                            )
                        }

                        marker?.showInfoWindow()
                    }

                    it.uiSettings.isScrollGesturesEnabled = true
                    it.uiSettings.isZoomGesturesEnabled = true
                    it.uiSettings.isMapToolbarEnabled = true
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
                            runStart = true
                            stopWatch.base = SystemClock.elapsedRealtime()
                            stopWatch.start()

                            mapView.visibility = View.VISIBLE
                            pauseButton.visibility = View.VISIBLE
                            pauseText.visibility = View.VISIBLE
                            timerScreen.visibility = View.VISIBLE


                            restartButton.setOnClickListener {
                                stopWatch.base = SystemClock.elapsedRealtime() - stopTime
                                stopWatch.start()

                                pauseText.visibility     = View.VISIBLE
                                pauseButton.visibility   = View.VISIBLE
                                restartText.visibility   = View.INVISIBLE
                                restartButton.visibility = View.INVISIBLE
                                finishButton.visibility  = View.GONE
                                finishText.visibility    = View.GONE

                                recordStop = true
                            }

                            pauseButton.setOnClickListener {
                                stopTime = SystemClock.elapsedRealtime() - stopWatch.base
                                stopWatch.stop()

                                pauseText.visibility      = View.INVISIBLE
                                pauseButton.visibility    = View.INVISIBLE
                                finishButton.visibility   = View.VISIBLE
                                finishText.visibility     = View.VISIBLE
                                restartText.visibility    = View.VISIBLE
                                restartButton.visibility  = View.VISIBLE

                                recordStop = false
                            }

                            finishButton.setOnClickListener {
//                                var chronometer = String.format("%02d:%02d:%02d",
//                                    TimeUnit.MILLISECONDS.toHours(stopWatch.base),
//                                    TimeUnit.MILLISECONDS.toHours(stopWatch.base),
//                                    TimeUnit.MILLISECONDS.toMinutes(stopWatch.base) -
//                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(stopWatch.base)),
//                                    TimeUnit.MILLISECONDS.toSeconds(stopWatch.base) -
//                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stopWatch.base))
//                                )
                                var chronometer = String.format("%02d",
                                    TimeUnit.MILLISECONDS.toHours(stopWatch.base))

                                Log.d("chronometer","$chronometer")

                                val builder = AlertDialog.Builder(requireContext())
                                builder
                                    .setCancelable(false)
                                    .setMessage("ランニングを終了しますか？")
                                    .setPositiveButton("YES",
                                        DialogInterface.OnClickListener{ dialog, id ->
                                            findNavController().navigate(R.id.action_navi_run_to_fragmentResult)
                                        })
                                    .setNegativeButton("CANCEL",
                                        DialogInterface.OnClickListener{ dialog, which ->
                                            dialog.dismiss()
                                        })
                                builder.show()
                                Log.d("dialog","$builder")
//                                lifecycleScope.launch(Dispatchers.IO) {
//                                    Log.d("read","${recordDao.getAll()}")
//                                    val record = Record(0,timeAmount kmAmount, ,calorieAmount,SystemClock.elapsedRealtime())
//                                    recordDao.insertRecord(record)
//
//                                    withContext(Dispatchers.Main) {
//
//                                    }
//                                }
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
            100f,
            0f,
            100f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply { duration = 1000 })
    }

//    private fun finishAction() {
//        val dialog = DialogMaker()
//        val supportFragment = parentFragmentManager
//        dialog.show(supportFragment,"kotlin")
//    }

    private fun kmConvert(distance: Double): Double {
        return ceil(distance) / 1000
    }

    private fun calorieConvert(distance: Double, weight: Double): Double {
        return ceil(distance) * weight / 1000
    }
}