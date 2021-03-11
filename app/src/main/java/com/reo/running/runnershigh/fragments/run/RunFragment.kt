package com.reo.running.runnershigh.fragments.run

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.*
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentRunBinding
import com.reo.running.runnershigh.fragments.common.*
import kotlinx.coroutines.*

class RunFragment : Fragment() {

    private lateinit var binding: FragmentRunBinding
    
    private val runViewModel: RunViewModel by viewModels {
        RunViewModel.Companion.Factory(
            MyApplication.db.justRunDao()
        )
    }
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val contentResolver: ContentResolver? = null

    companion object {
        private const val REQUEST_PERMISSION = 1000
        private const val PERMISSION_CODE = 1001
        private const val IMAGE_CAPTURE_CODE = 1002
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
            lifecycleOwner = viewLifecycleOwner
            viewModel = runViewModel
            if (checkSelfPermission(
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
            val locationRequest = LocationRequest().apply {
                interval = 1
                fastestInterval = 1
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val latLng = LatLng(
                        runViewModel.lastLocation.value?.latitude ?: 0.0,
                        runViewModel.lastLocation.value?.longitude ?: 0.0
                    )
                    mapView.getMapAsync {
                        it.isMyLocationEnabled = true
                        it.uiSettings.isMyLocationButtonEnabled = false
                        val alphaAnimation = AlphaAnimation(0f, 1f)
                        alphaAnimation.duration = 800
                        when (runViewModel.runState.value) {
                            RunState.RUN_STATE_BEFORE -> {
                                startNav.startAnimation(alphaAnimation)
                                startNav2.startAnimation(alphaAnimation)
                                it.animateCamera(
                                    CameraUpdateFactory
                                        .newLatLngZoom(
                                            latLng,
                                            runViewModel.zoomValue
                                        )
                                )
                            }

                            RunState.RUN_STATE_START -> {
                                it.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        latLng,
                                        runViewModel.zoomValue
                                    )
                                )
                                runViewModel.calcTotalMileage(locationResult?.lastLocation)
                            }

                            RunState.RUN_STATE_PAUSE -> {
                                startNav.run {
                                    visibility = View.VISIBLE
                                    setText(R.string.stop_Run)
                                    startAnimation(alphaAnimation)
                                }
                            }
                        }
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )

            startButton.setOnClickListener {
                runViewModel.setRunState(RunState.RUN_STATE_START)
                startNav.run {
                    visibility = View.GONE
                    clearAnimation()
                }
                startNav2.run {
                    visibility = View.GONE
                    clearAnimation()
                }
                startText.visibility = View.GONE
                startButton.visibility = View.GONE
                mapView.visibility = View.GONE
                (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE
                lifecycleScope.launch {
                    withContext(Dispatchers.Main) {
                        startButton.startAnimation(scaleUpAnimationMore {})
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
                    }
                        vibratorOn(VibrationType.LONG_VIBRATION)
                        startButton.clearAnimation()
                        stopWatch.base = SystemClock.elapsedRealtime()
                        stopWatch.start()
                        mapView.visibility = View.VISIBLE
                        pauseButton.visibility = View.VISIBLE
                        timerScreen.visibility = View.VISIBLE
                        lockOff.visibility = View.VISIBLE
                    }

            }

            restartButton.setOnClickListener {
                runViewModel.setRunState(RunState.RUN_STATE_START)
                vibratorOn(VibrationType.LONG_VIBRATION)
                stopWatch.base = SystemClock.elapsedRealtime() - (runViewModel.stopTime.value ?: 0L)
                stopWatch.start()
                finishButton.visibility = View.GONE
                startNav.run {
                    visibility = View.GONE
                    clearAnimation()
                }
                restartButton.startAnimation(scaleDownAnimation(
                    animationEndOperation = {
                        restartButton.clearAnimation()
                        restartButton.visibility = View.GONE
                        lockOff.visibility = View.VISIBLE
                        pauseButton.visibility = View.VISIBLE
                        pauseButton.startAnimation(scaleUpAnimation(
                            operation = { it.fillAfter = false }
                        ))
                    }
                ))
            }

            pauseButton.setOnClickListener {
                runViewModel.setRunState(RunState.RUN_STATE_PAUSE)
                vibratorOn(VibrationType.MIDDLE_VIBRATION)
                runViewModel.stopTime.value = SystemClock.elapsedRealtime() - stopWatch.base
                stopWatch.stop()
                pauseButton.startAnimation(scaleDownAnimation(
                    animationEndOperation = {
                        pauseButton.clearAnimation()
                        pauseButton.visibility = View.GONE
                        lockOff.visibility = View.GONE
                        finishButton.visibility = View.VISIBLE
                        restartButton.visibility = View.VISIBLE
                        restartButton.startAnimation(scaleUpAnimation {})
                        finishButton.startAnimation(scaleUpAnimation(
                            operation = { it.fillAfter = false }
                        ))
                    }
                ))

            }

            finishButton.setOnClickListener {
                vibratorOn(VibrationType.SHORT_VIBRATION)
                finishButton.startAnimation(scaleDownAnimation(animationEndOperation = {
                    finishButton.clearAnimation()
                    AlertDialog.Builder(requireContext())
                        .setCancelable(false)
                        .setMessage("ランニングを終了しますか？")
                        .setPositiveButton("YES") { _, _ ->
                            runViewModel.saveRunData {
                                findNavController().navigate(R.id.action_navi_run_to_fragmentResult)
                            }
                        }
                        .setNegativeButton(
                            "CANCEL"
                        ) { _, _ ->
                        }.show()
                }))
            }

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
                if (checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        Manifest.permission.CAMERA
                    )
                    requestPermissions(permission, PERMISSION_CODE)
                } else {
                    openCamera()
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

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        runViewModel.imageUri.value =
            contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, runViewModel.imageUri.value)
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
            (data?.extras?.get("data") as? Bitmap?)?.let {
                binding.cameraSet.setImageBitmap(it)
                binding.cameraSet.rotation = 90f
                runViewModel.takenPhoto.value = it
                runViewModel.isTakenPhoto.value = true
            }
        }
    }
}