package com.reo.running.runnershigh.fragments

import android.Manifest
import android.R
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.reo.running.runnershigh.MainActivity
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*

class Fragment1 : Fragment() {

    private lateinit var binding:Fragment1Binding
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    var stdLocation:Location? = null
    var totalDistance:Int = 0
    var cnt:Int = 0
    var results = FloatArray(1)
    var start = true

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
                    val zoomValue = 30.0f
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude, lastLocation.longitude), zoomValue))
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

                    if (cnt < 10) {
                        totalDistance = 0
                        cnt++
                        println("count")
                    }
                }
                stdLocation = lastLocation
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
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

        binding.start.setOnClickListener{
            (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
            findNavController().navigate(com.reo.running.runnershigh.R.id.action_navi_run_to_countDown)
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

}