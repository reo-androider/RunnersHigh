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
    var gpsAdjust = 0
    var weight = 57.0
    var kmAmount: Double = 0.0
    var calorieAmount: Double = 0.0
    var recordStop = true
    private var stopTime: Long = 0L
    private val recordDao = MyApplication.db.recordDao()
    var marker: Marker? = null
    var runStart = false
    var leaveFootprints = 0
    var gpsSearch = 0

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


                    if (gpsAdjust < 8) {
                        Log.d("gpsCount", "$gpsAdjust")
                        totalDistance = 0.0
                        gpsAdjust++
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

                    if (gpsAdjust > 10) {

                        if (runStart == false) {

                            if (gpsSearch == 10) {
                                binding.gpsSearch.visibility = View.GONE
                                binding.startNav.visibility = View.VISIBLE
                                binding.startNav2.visibility = View.VISIBLE
                                binding.startText.visibility = View.VISIBLE
                                binding.startButton.visibility = View.VISIBLE
                            }

                            marker?.remove()
                            marker = it.addMarker(
                                MarkerOptions().position(LatLng(lastLocation.latitude, lastLocation.longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(Resource.getBitmap(context, R.drawable.ic_running)))
                                    .title("You are here"))

                            gpsSearch++
                        }

                        else if (runStart == true) {
                            Log.d("duraring_run","OK")
                            if (leaveFootprints % 5 == 0) {
                                marker = it.addMarker(
                                    MarkerOptions().position(
                                        LatLng(lastLocation.latitude, lastLocation.longitude))
                                        .icon(BitmapDescriptorFactory.fromBitmap(Resource.getBitmap(context, R.drawable.ic_running))))
                                Log.d("leaveFoorPrints","$leaveFootprints")
                            }
                            leaveFootprints++
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
                GlobalScope.launch(Dispatchers.Main) {
                    binding.mapView.visibility = View.INVISIBLE
                    countDown5()
                    delay(1000)
                    countDown4()
                    delay(1000)
                    countDown3()
                    delay(1000)
                    countDown2()
                    delay(1000)
                    countDown1()
                    delay(1000)
                    countDown0()
                    binding.mapView.visibility = View.VISIBLE
                }
                binding.run {
                    startText.visibility = View.GONE
                    startButton.visibility = View.GONE

                    (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
                    GlobalScope.launch {
                        withContext(Dispatchers.Main) {
                            view.startAnimation(ScaleAnimation(0f, 100f, 0f, 100f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f))
//                            listOf(
//                                countNum5,
//                                countNum4,
//                                countNum3,
//                                countNum2,
//                                countNum1,
//                                countNum0
//                            ).map {
//                                viewVisible(it)
//                                animationCount(it)
//                                delay(1000)
//                            }
                        }

                        withContext(Dispatchers.Main) {
                            runStart = true
                            startNav.visibility = View.GONE
                            startNav2.visibility = View.GONE
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

//    private fun animationCount(view: View) {
//        view.startAnimation(ScaleAnimation(
//            0f,
//            100f,
//            0f,
//            100f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f,
//            Animation.RELATIVE_TO_SELF,
//            0.5f
//        ).apply { duration = 1000 })
//    }


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


    fun countDown5() {
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
        scale.duration = 3000
        binding.countNum5.startAnimation(scale)
    }

    fun countDown4() {
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
        scale.duration = 3000
        binding.countNum4.startAnimation(scale)
    }

    fun countDown3() {
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
        scale.duration = 3000
        binding.countNum3.startAnimation(scale)
    }

    fun countDown2() {
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
        scale.duration = 3000
        binding.countNum2.startAnimation(scale)
    }

    fun countDown1() {
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
        scale.duration = 3000
        binding.countNum1.startAnimation(scale)
    }

    fun countDown0() {
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
        scale.duration = 3000
        binding.countNum0.startAnimation(scale)
    }
}