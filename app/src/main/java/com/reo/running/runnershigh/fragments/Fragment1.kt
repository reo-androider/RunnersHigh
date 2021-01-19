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
import com.google.android.gms.maps.model.*
import com.reo.running.runnershigh.databinding.Fragment1Binding
import kotlinx.coroutines.*

class Fragment1 : Fragment() {
   // var map:GoogleMap? = null
    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    var total:Double = 0.0
    // 基準値緯度経度
    var stdLct:LatLng? = null
    var lastLct:LatLng? = null
    var stdLocation:Location? = null
    var totalDistance:Int = 0
    var distance:Float? = null
//    var locations:LocationResult?.locations = null
//    var startLatitude:Double = 0.0
//    var startLongtude:Double = 0.0
    var results = FloatArray(1)
   // var switch = true

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
             interval = 1                                   //最遅の更新間隔（ただし正確ではない）
             fastestInterval = 1                            //最短の更新間隔
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
                 val lastLocation = locationResult?.lastLocation ?: return
                stdLocation?.let {
                    Location.distanceBetween(
                        it.latitude, it.longitude,
                        lastLocation.latitude, lastLocation.longitude, results
                    )
                    totalDistance += results[0].toInt()
                    println(totalDistance)
                }

                stdLocation = lastLocation

//                println(locations)
//                println(locations[0].latitude)


                    binding.mapView.getMapAsync {
                        // zoom-in
                        val zoomValue = 20.0f
                        var latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomValue))
                        //it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude),20f))
                        // 平行移動可能に
                        it.uiSettings.isScrollGesturesEnabled = true
                        // 縮尺変更
                        it.uiSettings.isZoomGesturesEnabled = true
                        // Marker押すとでてくるよ
                        it.uiSettings.isMapToolbarEnabled = true
                        //コンパス
                        it.uiSettings.isCompassEnabled = true
                        // 現在地ボタン
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

////                                //遅延処理
//                                  GlobalScope.launch(Dispatchers.Main) {
//                                      delay(60000)
////                                     //基準となる緯度・経度
//                                      val stdLct = LatLng(locations[0].latitude, locations[0].longitude)
//                                      Log.d("std","$stdLct")
////                                     //基準となる緯度・経度の地点にマーカーを指す
//                                      it.addMarker(MarkerOptions()
//                                          .position(stdLct)
//                                          .icon(BitmapDescriptorFactory.fromBitmap(Resource.getBitmap(context, R.drawable.in_trace,))))
//                                  }
//                            var newLct = LatLng(locations[0].latitude,locations[0].longitude)
//                            Log.d("new","$newLct")
//                            stdLct?.let {
//                                distance = SphericalUtil.computeDistanceBetween(stdLct,newLct)
//                            }
//                            //TODO 0と表示される
//                            binding.meter.text = "$distance"
//                            Log.d("distance","$distance")


//                            distance = SphericalUtil.computeDistanceBetween(LatLng(locations[10].latitude,locations[10].longitude),LatLng(locations[0].latitude,locations[0].longitude))

//                            total = SphericalUtil.computeDistanceBetween(LatLng(locations.first().latitude,locations.first().longitude),
//                                LatLng(locations[0].latitude,locations[0].longitude)
//                            )
//                            val a = LatLng(locations[0].latitude,locations[0].longitude)
//                            Handler().postDelayed(Runnable {
//                                val b = LatLng(locations[0].latitude,locations[0].longitude)
//                            },6000)
//                            distance = Location.distanceBetween(a,a2,b,b2,results)
                                // 移動距離をTextViewに表示

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