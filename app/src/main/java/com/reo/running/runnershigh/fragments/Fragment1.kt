package com.reo.running.runnershigh.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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


class Fragment1 : Fragment() {
   // var map:GoogleMap? = null
    private lateinit var binding: Fragment1Binding
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    var distance:Double = 0.0
    var total:Double = 0.0
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
                // TODO 3Dにする
                // TODO 方角を自分が向いている方向にする
                //マーカーを指した箇所の緯度経度を取って置く。
                    binding.mapView.getMapAsync{
                        // zoom-in
                        val zoomValue = 30.0f
                        var latLng = LatLng(location.latitude,location.longitude)
                        it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoomValue)
                        )
                        //marker
                        it.addMarker(
                            MarkerOptions()
                                .position(LatLng(location.latitude,location.longitude))
                        //        .icon(BitmapDescriptorFactory.fromBitmap(Resource.getBitmap(context, R.drawable.in_trace,)))
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
                        }

                        // 基準となる緯度・経度
                        var stdLct = LatLng(location.latitude,location.longitude)

                         //遅延処理
//                        Looper.myLooper()?.let {
//                            Handler(it).postDelayed({
//                                // 最新の緯度経度
//                                var newLct = LatLng(location.latitude,location.longitude)
//                                distance = SphericalUtil.computeDistanceBetween(stdLct, newLct)
//                                total += distance
//                                                    },3000000)
//                        }

                        // 線の描画
                        it.addPolyline(
                            PolylineOptions()
                                .add(stdLct)
                                .add(LatLng(location.latitude,location.longitude))
                                .color(Color.RED)
                                .width(10f)
                        )
                      //  binding.meter.setText("$total")

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