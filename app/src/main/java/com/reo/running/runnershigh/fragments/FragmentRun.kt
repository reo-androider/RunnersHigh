package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
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
    val zoomValue = 20.0f
    var gpsAdjust = 10
    var weight = 57.0
    var kmAmount: Double = 0.0
    var calorieAmount: Int = 0
    var recordStop = true
    private var stopTime: Long = 0L
    private val recordDao = MyApplication.db.recordDao()
    var marker: Marker? = null
    private var runStart = false
    private lateinit var vibrator: Vibrator
    private lateinit var vibrationEffect: VibrationEffect
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var image_uri: Uri? = null
    private val contentResolver: ContentResolver? = null
    private var photo:Bitmap? = null
    private var takePhoto = false
    private var  countStart = false //アニメーションを何度も再生されないように

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
//        binding.gpsSearch.visibility = View.VISIBLE
//        binding.sleepBat.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            val waveAnimation = TranslateAnimation(
                1f,
                1f,
                1f,
                -100f
            )
            waveAnimation.let {
                it.duration = 100
            }

//            while (gpsAdjust < 10) {
//                binding.S.startAnimation(waveAnimation)
//                delay(100)
//                binding.S.clearAnimation()
//                binding.e.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.e.clearAnimation()
//                binding.a.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.a.clearAnimation()
//                binding.r.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.r.clearAnimation()
//                binding.c.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.c.clearAnimation()
//                binding.h.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.h.clearAnimation()
//                binding.F.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.F.clearAnimation()
//                binding.o.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.o.clearAnimation()
//                binding.r2.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.r2.clearAnimation()
//                binding.G.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.G.clearAnimation()
//                binding.P.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.P.clearAnimation()
//                binding.S2.startAnimation(waveAnimation)
//
//                delay(100)
//                binding.S2.clearAnimation()
//                delay(1000)
//            }
        }
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
                    it.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastLocation.latitude,
                                lastLocation.longitude
                            ), zoomValue
                        )
                    )

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

                        marker?.remove()
                        marker = it.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    lastLocation.latitude,
                                    lastLocation.longitude
                                )
                            )
                        )

                    } else {

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
                                            R.drawable.in_trace
                                        )
                                    )
                                )
                        )
                    }

                    if (gpsAdjust < 10) {

                        Log.d("gpsCount", "$gpsAdjust")

                        totalDistance = 0.0

                    } else {

                        if (gpsAdjust == 10) {
//                            binding.sleepBat.visibility = View.GONE
                            binding.mapView.visibility = View.VISIBLE
//                            binding.gpsSearch.visibility = View.GONE
                            binding.startNav.visibility = View.VISIBLE
                            binding.startNav2.visibility = View.VISIBLE

                            binding.startText.visibility = View.VISIBLE
                            binding.centerCircle.visibility = View.VISIBLE
//
//                            binding.S.visibility = View.GONE
//                            binding.e.visibility = View.GONE
//                            binding.a.visibility = View.GONE
//                            binding.r.visibility = View.GONE
//                            binding.c.visibility = View.GONE
//                            binding.h.visibility = View.GONE
//                            binding.F.visibility = View.GONE
//                            binding.o.visibility = View.GONE
//                            binding.r2.visibility = View.GONE
//                            binding.G.visibility = View.GONE
//                            binding.P.visibility = View.GONE
//                            binding.S2.visibility = View.GONE
//                            binding.sleepBat.visibility = View.GONE

                            val alphaAnimation = AlphaAnimation(0f, 1f)
                            alphaAnimation.duration = 500

                            val alphaAnimation2 = AlphaAnimation(0f, 1f)
                            alphaAnimation2.duration = 500

                            binding.startNav.startAnimation(alphaAnimation)
                            binding.startNav2.startAnimation(alphaAnimation)

                            binding.centerCircle.startAnimation(alphaAnimation2)
                            binding.startText.startAnimation(alphaAnimation2)

                        }

                        marker?.showInfoWindow()
                    }

                    it.uiSettings.isScrollGesturesEnabled = true
                    it.uiSettings.isZoomGesturesEnabled = true
                    it.uiSettings.isMapToolbarEnabled = true
                    it.uiSettings.isCompassEnabled = true

                    gpsAdjust++

                    Log.d("gsp", "$gpsAdjust")

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

            if (countStart == false) {
                countStart = true
                binding.centerCircle.setOnClickListener {
                    recordStop = false
                    binding.run {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val scaleStartButton = ScaleAnimation(
                            1f,
                            100f,
                            1f,
                            100f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f,
                            Animation.RELATIVE_TO_SELF,
                            0.5f
                        )
                        scaleStartButton.let {
                            it.duration = 1500
                            it.fillAfter = true
                        }

                        mapView.visibility = View.GONE
                        startText.visibility = View.GONE
                        centerCircle.visibility = View.GONE
                        startNav.visibility = View.GONE
                        startNav2.visibility = View.GONE

                        delay(50)
                        binding.centerCircle.startAnimation(scaleStartButton)
                        delay(1000)

                    }


                        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

                        GlobalScope.launch {
                            withContext(Dispatchers.IO) {
                                delay(1000)
                                Log.d("withContext", "withContext")
                                listOf(
                                    countNum3,
                                    countNum2,
                                    countNum1,
                                ).map {
                                    animationCount(it)
                                    delay(1000)
                                }
                            }

                            GlobalScope.launch(Dispatchers.Main) {
                                vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                vibrationEffect = VibrationEffect.createOneShot(1000, 255)
                                vibrator.vibrate(vibrationEffect)
                                runStart = true
                                centerCircle.clearAnimation()
                                startNav.visibility = View.GONE
                                startNav2.visibility = View.GONE
                                stopWatch.base = SystemClock.elapsedRealtime()
                                stopWatch.start()

                                mapView.visibility = View.VISIBLE
                                pauseImage.visibility = View.VISIBLE
                                pauseButton.visibility = View.VISIBLE
                                timerScreen.visibility = View.VISIBLE
                                lockOff.visibility = View.VISIBLE

                                lockOff.setOnClickListener {
                                    lockOff.visibility = View.GONE
                                    pauseImage.visibility = View.GONE
                                    pauseButton.visibility = View.GONE
                                    lockImage.visibility = View.VISIBLE
                                }

                                lockImage.setOnClickListener {
                                    lockImage.visibility = View.GONE
                                    pauseImage.visibility = View.VISIBLE
                                    pauseButton.visibility = View.VISIBLE
                                    lockOff.visibility = View.VISIBLE

                                }

                                cameraImage.setOnClickListener {
                                    if (checkSelfPermission(Manifest.permission.CAMERA)
                                        == PackageManager.PERMISSION_DENIED ||
                                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_DENIED
                                    ) {
                                        val permission = arrayOf(
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE
                                        )
                                        requestPermissions(permission, PERMISSION_CODE)

                                    } else {
                                        openCamera()
                                    }
                                }


                                // TODO 赤字になる
//                            lockImage.visibility = View.GONE

                                restartButton.setOnClickListener {
                                    vibrator =
                                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrationEffect = VibrationEffect.createOneShot(800, 255)
                                    vibrator.vibrate(vibrationEffect)
                                    stopWatch.base = SystemClock.elapsedRealtime() - stopTime
                                    stopWatch.start()
                                    recordStop = false
                                    finishButton.visibility = View.GONE
                                    finishImage.visibility = View.GONE

                                    GlobalScope.launch(Dispatchers.Main) {
                                        val scaleRestartImage = ScaleAnimation(
                                            1f,
                                            0.6f,
                                            1f,
                                            0.6f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f
                                        )

                                        val scaleRestartButton = ScaleAnimation(
                                            1f,
                                            0.6f,
                                            1f,
                                            0.6f,
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
                                        restartImage.startAnimation(scaleRestartImage)
                                        restartButton.startAnimation(scaleRestartButton)
                                        delay(600)
                                        lockOff.visibility = View.VISIBLE
                                        restartImage.clearAnimation()
                                        restartButton.clearAnimation()
                                        pauseImage.visibility = View.VISIBLE
                                        pauseButton.visibility = View.VISIBLE
                                        restartImage.visibility = View.GONE
                                        restartButton.visibility = View.GONE
                                        val scalePauseImage = ScaleAnimation(
                                            0.1f,
                                            1f,
                                            0.1f,
                                            1f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f
                                        )

                                        val scalePauseButton = ScaleAnimation(
                                            0.6f,
                                            1f,
                                            0.6f,
                                            1f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f
                                        )

                                        scalePauseImage.let {
                                            it.duration = 300
                                        }

                                        scalePauseButton.let {
                                            it.duration = 300
                                        }

                                        pauseImage.startAnimation(scalePauseImage)
                                        pauseButton.startAnimation(scalePauseButton)

                                    }
                                }

                                pauseButton.setOnClickListener {
                                    lockOff.visibility = View.GONE
                                    vibrator =
                                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrationEffect = VibrationEffect.createOneShot(500, 255)
                                    vibrator.vibrate(vibrationEffect)
                                    recordStop = true
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
                                    vibrator =
                                        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                    vibrationEffect = VibrationEffect.createOneShot(600, 255)
                                    vibrator.vibrate(vibrationEffect)
                                    GlobalScope.launch(Dispatchers.Main) {
                                        val scaleFinishImage = ScaleAnimation(
                                            1f,
                                            0.6f,
                                            1f,
                                            0.6f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f
                                        )

                                        val scaleFinishButton = ScaleAnimation(
                                            1f,
                                            0.6f,
                                            1f,
                                            0.6f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f,
                                            Animation.RELATIVE_TO_SELF,
                                            0.5f
                                        )

                                        scaleFinishImage.let {
                                            it.duration = 300
                                            it.fillAfter = true
                                        }

                                        scaleFinishButton.let {
                                            it.duration = 300
                                            it.fillAfter = true
                                        }

                                        finishImage.startAnimation(scaleFinishImage)
                                        finishButton.startAnimation(scaleFinishButton)

                                        delay(500)

                                        finishImage.clearAnimation()
                                        finishButton.clearAnimation()

                                        Log.d("test", stopWatch.text.toString())

                                        val builder = AlertDialog.Builder(requireContext())
                                        builder
                                            .setCancelable(false)
                                            .setMessage("ランニングを終了しますか？")
                                            .setPositiveButton("YES") { _, _ ->
                                                    lifecycleScope.launch(Dispatchers.IO) {
                                                        val record = Record(
                                                            0,
                                                            stopWatch.text.toString(),
                                                            kmAmount,
                                                            calorieAmount,
                                                            getRunDate(),
                                                            photo,
                                                            takePhoto
                                                        )
                                                        recordDao.insertRecord(record)
                                                        Log.d("room", "${recordDao.getAll()}")
                                                        withContext(Dispatchers.Main) {
                                                            findNavController().navigate(R.id.action_navi_run_to_fragmentResult)
                                                        }
                                                    }
                                                }
                                            .setNegativeButton(
                                                "CANCEL"
                                            ) { _, _ ->
                                            }
                                        builder.show()
                                    }
                                }
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
        ).apply { duration = 1000 })
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

    private fun openCamera() {
        // TODO
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_DENIED
                ) {
                    openCamera()
                } else {
                    Toast.makeText(context, "Permission dined", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("photo_camera", "$data")
        Log.d("photo_camera", "$image_uri")
        if (resultCode == Activity.RESULT_OK) {
                (data?.extras?.get("data") as? Bitmap?).let {
                    binding.cameraSet.setImageBitmap(it)
                    binding.cameraSet.rotation = 90f
                    photo = it
                    takePhoto = true
                    Log.d("photo","$photo")
                }
        }
    }
}

