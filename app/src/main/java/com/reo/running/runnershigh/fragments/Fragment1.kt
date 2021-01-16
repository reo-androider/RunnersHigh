package com.reo.running.runnershigh.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdate
//import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.mapbox.mapboxsdk.style.expressions.Expression.color
import com.mapbox.mapboxsdk.style.expressions.Expression.id
import com.reo.running.runnershigh.BitmapHelper
import com.reo.running.runnershigh.databinding.Fragment1Binding

class Fragment1 : Fragment() {
    var map:GoogleMap? = null
    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

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
            //今回は公式のサンプル通りにする
             interval = 3                                   //最遅の更新間隔（ただし正確ではない）
             fastestInterval = 3                             //最短の更新間隔
             priority = LocationRequest.PRIORITY_HIGH_ACCURACY   //精度重視
            //切り替え実装
            //ピンの間隔で距離計計測
            //marker 緯度経度から距離を計算する
            //足跡付ける
        }

        //コールバック
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                //更新直後の位置が格納されているはず
                 val location = locationResult?.lastLocation ?: return
               // Location.distanceBetween()//lastlocation
                binding.mapView.getMapAsync{
                    it.addMarker(MarkerOptions()
                            .position(LatLng(location.latitude,location.longitude)))
//                    it.moveCamera(CameraUpdateFactory())
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
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback,Looper.myLooper())
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