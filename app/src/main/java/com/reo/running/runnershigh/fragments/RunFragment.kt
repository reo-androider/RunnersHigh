package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.Context.VIBRATOR_SERVICE
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentRunBinding
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.ceil

class RunFragment : Fragment() {

    private lateinit var binding: FragmentRunBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var stdLocation: Location? = null
    var totalDistance = 0.0
    var results = FloatArray(1)
    val zoomValue = 20.0f
    var gpsAdjust = 10
    var weight = 60.0
    var kmAmount: Double = 0.0
    var calorieAmount: Int = 0
    var recordStop = false
    private var stopTime: Long = 0L
    private val recordDao = MyApplication.db.justRunDao()
    var marker: Marker? = null
    private var runStart = false
    private lateinit var vibrator: Vibrator
    private lateinit var vibrationEffect: VibrationEffect
    private var imageUri: Uri? = null
    private val contentResolver: ContentResolver? = null
    private var photo: Bitmap? = null
    private var takePhoto = false
    private var  countStart = false //アニメーションが何度も再生されないように
    companion object {
        private const val REQUEST_PERMISSION = 1
        private const val PERMISSION_CODE = 1000
        private const val IMAGE_CAPTURE_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentRunBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_PERMISSION
                )
                return
            }
            mapView.onCreate(savedInstanceState)
                val databaseRefWeight = Firebase.database.getReference("weight")
                databaseRefWeight.setValue("")
                databaseRefWeight.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val myWeight = snapshot.value
                        if (myWeight.toString() != "") weight = myWeight.toString().toDouble()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            val locationRequest = LocationRequest().apply {
                interval = 1
                fastestInterval = 1
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val lastLocation = locationResult?.lastLocation ?: return
                    mapView.getMapAsync {
                        it.isMyLocationEnabled = true
                        stdLocation?.let {
                            Location.distanceBetween(
                                    it.latitude,
                                    it.longitude,
                                    lastLocation.latitude,
                                    lastLocation.longitude,
                                    results
                            )

//                            if (recordStop) {
//                                stdLocation?.let {
//                                    Location.distanceBetween(
//                                            it.latitude,
//                                            it.longitude,
//                                            lastLocation.latitude,
//                                            lastLocation.longitude,
//                                            results
//                                    )
//                                }

                                totalDistance += results[0]
                                stdLocation = lastLocation

                                kmAmount = kmConvert(totalDistance)
                                calorieAmount = calorieConvert(totalDistance, weight)
                                distance.text = "$kmAmount"
                                calorieNum.text = "$calorieAmount"

//                                marker?.remove()
//                                marker = it.addMarker(
//                                        MarkerOptions().position(
//                                                LatLng(
//                                                        lastLocation.latitude,
//                                                        lastLocation.longitude
//                                                )
//                                        )
//                                )
//                            } else {
//                                marker?.remove()
//                                marker = it.addMarker(
//                                        MarkerOptions().position(
//                                                LatLng(
//                                                        lastLocation.latitude,
//                                                        lastLocation.longitude
//                                                )
//                                        )
//                                                .icon(
//                                                        BitmapDescriptorFactory
//                                                                .fromBitmap(
//                                                                        BitmapConverter.getBitmap(
//                                                                                context,
//                                                                                R.drawable.ic_trace
//                                                                        )
//                                                                )
//                                                )
//                                )
                            }

                            if (gpsAdjust < 10) {
                                totalDistance = 0.0
                            } else {
                                if (gpsAdjust == 10) {
                                    mapView.visibility = View.VISIBLE
                                    startNav.visibility = View.VISIBLE
                                    startNav2.visibility = View.VISIBLE
                                    startText.visibility = View.VISIBLE
                                    centerCircle.visibility = View.VISIBLE

                                    val alphaAnimation = AlphaAnimation(0f, 1f)
                                    alphaAnimation.duration = 500
                                    startNav.startAnimation(alphaAnimation)
                                    startNav2.startAnimation(alphaAnimation)
                                    centerCircle.startAnimation(alphaAnimation)
                                    startText.startAnimation(alphaAnimation)

                                }

                                marker?.showInfoWindow()
                            }
                            gpsAdjust++
                        }
                    }
                }

                requireContext().run {
                    fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()
                    )

                    if (!countStart) {
                        countStart = true
                        binding.centerCircle.setOnClickListener {
                            recordStop = false
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
                                    centerCircle.startAnimation(scaleStartButton)
                                    delay(1000)

                                }

                                (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

                                GlobalScope.launch {
                                    withContext(Dispatchers.IO) {
                                        delay(1000)
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
                                        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                                        vibrationEffect = VibrationEffect.createOneShot(1000, 255)
                                        vibrator.vibrate(vibrationEffect)
                                        runStart = true
                                        centerCircle.clearAnimation()
                                        startNav.visibility = View.GONE
                                        startNav2.visibility = View.GONE
                                        stopWatch.base = SystemClock.elapsedRealtime()
                                        stopWatch.start()

                                        mapView.visibility = View.VISIBLE
                                        pauseButton.visibility = View.VISIBLE
                                        timerScreen.visibility = View.VISIBLE
                                        lockOff.visibility = View.VISIBLE

                                        lockOff.setOnClickListener {
                                            lockOff.visibility = View.GONE
                                            pauseButton.visibility = View.GONE
                                            lockImage.visibility = View.VISIBLE
                                        }

                                        lockImage.setOnClickListener {
                                            lockImage.visibility = View.GONE
                                            pauseButton.visibility = View.VISIBLE
                                            lockOff.visibility = View.VISIBLE

                                        }

                                        cameraImage.setOnClickListener {
                                            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                                                val permission = arrayOf(
                                                        Manifest.permission.CAMERA
                                                )
                                                requestPermissions(permission, PERMISSION_CODE)
                                            } else {
                                                openCamera()
                                            }
                                        }

                                        restartButton.setOnClickListener {
                                            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                                            vibrationEffect = VibrationEffect.createOneShot(800, 255)
                                            vibrator.vibrate(vibrationEffect)
                                            stopWatch.base = SystemClock.elapsedRealtime() - stopTime
                                            stopWatch.start()
                                            recordStop = false
                                            finishButton.visibility = View.GONE
                                            GlobalScope.launch(Dispatchers.Main) {
                                                restartButton.startAnimation(scaleDownAnimation {
                                                    it.duration = 300
                                                    it.fillAfter = true
                                                })
                                                delay(500)
                                                restartButton.clearAnimation()
                                                restartButton.visibility = View.GONE
                                                lockOff.visibility = View.VISIBLE
                                                pauseButton.visibility = View.VISIBLE
                                                pauseButton.startAnimation(scaleUpAnimation {
                                                    it.duration = 300
                                                    it.fillAfter = true
                                                })
                                            }
                                        }

                                        pauseButton.setOnClickListener {
                                            lockOff.visibility = View.GONE
                                            vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                                            vibrationEffect = VibrationEffect.createOneShot(500, 255)
                                            vibrator.vibrate(vibrationEffect)
                                            recordStop = true
                                            stopTime = SystemClock.elapsedRealtime() - stopWatch.base
                                            stopWatch.stop()

                                            GlobalScope.launch(Dispatchers.Main) {
                                                pauseButton.startAnimation(scaleDownAnimation {
                                                    it.duration = 300
                                                    it.fillAfter = true
                                                })
                                                delay(500)
                                                pauseButton.clearAnimation()
                                                pauseButton.visibility = View.INVISIBLE
                                                finishButton.visibility = View.VISIBLE
                                                restartButton.visibility = View.VISIBLE
                                                restartButton.startAnimation(scaleUpAnimation {
                                                    it.duration = 300
                                                })
                                                finishButton.startAnimation(scaleUpAnimation {
                                                    it.duration = 300
                                                })
                                                delay(300)
                                            }
                                        }

                                        finishButton.setOnClickListener {
                                            vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                                            vibrationEffect = VibrationEffect.createOneShot(600, 255)
                                            vibrator.vibrate(vibrationEffect)
                                            GlobalScope.launch(Dispatchers.Main) {

                                                finishButton.startAnimation(scaleDownAnimation {
                                                    it.duration = 300
                                                    it.fillAfter = true
                                                })
                                                delay(500)
                                                finishButton.clearAnimation()

                                                val builder = AlertDialog.Builder(requireContext())
                                                builder
                                                        .setCancelable(false)
                                                        .setMessage("ランニングを終了しますか？")
                                                        .setPositiveButton("YES") { _, _ ->
                                                            lifecycleScope.launch(Dispatchers.IO) {
                                                                val record = JustRunData(
                                                                        0,
                                                                        stopWatch.text.toString(),
                                                                        kmAmount,
                                                                        calorieAmount,
                                                                        getRunDate(),
                                                                        photo,
                                                                        takePhoto
                                                                )
                                                                recordDao.insertRecord(record)
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

    private fun scaleUpAnimation(operation: (ScaleAnimation) -> Unit = {}): ScaleAnimation =
            ScaleAnimation(
                    0.6f,
                    1f,
                    0.6f,
                    1f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            ).apply {
                operation(this)
            }

    private fun scaleDownAnimation(operation: (ScaleAnimation) -> Unit = {}): ScaleAnimation =
            ScaleAnimation(
                    1f,
                    0.6f,
                    1f,
                    0.6f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            ).apply {
                operation(this)
            }

    private fun clearAnimation(binding: FragmentRunBinding) {
        binding.run {
            restartButton.clearAnimation()
            finishButton.clearAnimation()
        }
    }

    private fun animationCount(view: View) {
        view.startAnimation(ScaleAnimation(
            0f,
            400f,
            0f,
            400f,
            Animation.RELATIVE_TO_SELF,
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
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        imageUri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
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
                    Toast.makeText(context, "Permission dined", Toast.LENGTH_SHORT).show()

                } else {
                    openCamera()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
                (data?.extras?.get("data") as? Bitmap?).let {
                    binding.cameraSet.setImageBitmap(it)
                    binding.cameraSet.rotation = 90f
                    photo = it
                    takePhoto = true
                }
        }
    }
}