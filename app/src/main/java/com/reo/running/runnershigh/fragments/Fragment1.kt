package com.reo.running.runnershigh.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.reo.running.runnershigh.databinding.Fragment1Binding
import com.reo.running.runnershigh.Resource
import java.util.*


class Fragment1 : Fragment() {
    var map:GoogleMap? = null
    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    var distance:Double? = null
    var total:Double = 0.0

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
             interval = 500                                   //最遅の更新間隔（ただし正確ではない）
             fastestInterval = 500                            //最短の更新間隔
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
                //マーカーを指した箇所の緯度経度を取って置く。
                var pre = LatLng(location.latitude,location.longitude)
                    binding.mapView.getMapAsync{
                        it.addMarker(
                            MarkerOptions()
                                .position(pre)
                        //        .icon(BitmapDescriptorFactory.fromBitmap(Resource.getBitmap(context, R.drawable.in_trace,)))
                        )
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),20f))
                        var now = pre
                        distance = SphericalUtil.computeDistanceBetween(pre, now)
                        binding.distance.setText("$total")
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