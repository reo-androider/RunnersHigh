package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.MyAdapter
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentResultBinding
import kotlinx.coroutines.*

class FragmentResult : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val readDao = MyApplication.db.recordDao()
    private var memo = ""
    private var select = false//二回押しても同じアニメーションが実行されない為
    private var selectMark = ""
    private var image_uri: Uri? = null
    private val contentResolver: ContentResolver? = null
    private var position = 0

    companion object {
        const val PERMISSION_CODE = 1
        const val IMAGE_CAPTURE_CODE = 2
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentResultBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("delete", "${readDao.getAll()}")
            Log.d("delete", "${readDao.getAll().last()}")
            Log.d("delete", readDao.getAll().last().time)
            Log.d("delete", "${readDao.getAll().last().distance}")
            Log.d("delete", "${readDao.getAll().last().calorie}")

            val record = readDao.getAll()

            withContext(Dispatchers.Main) {
                binding.totalTime.text = record.last().time
                binding.totalDistance.text = "${record.last().distance}km"
                binding.totalCalorie.text = "${record.last().calorie}kcal"
                binding.today.text = record.last().runDate
                binding.photoEmpty.setImageBitmap(record.last().bitmap)

                if (record.last().takenPhoto) {
                    binding.cameraImage.visibility = View.GONE
                }

                binding.cameraImage.setOnClickListener {
                        if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED ||
                            ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED
                        ){
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

                binding.perfectImage.alpha = 0.6F
                binding.goodImage.alpha = 0.6F
                binding.sosoImage.alpha = 0.6F
                binding.badImage.alpha = 0.6F
                binding.tooBadImage.alpha = 0.6F

                binding.perfectImage.setOnClickListener {
                    if (!select) {
                        select = true
                        selectMark = "perfect"
                        lifecycleScope.launch(Dispatchers.Main) {
                            val scaleAnimation = ScaleAnimation(
                                1f,
                                0.3f,
                                1f,
                                0.3f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                                it.fillAfter = true
                            }

                            binding.perfectImage.alpha = 1F
                            binding.perfectImage.setColorFilter(Color.parseColor("#4CAF50"))
                            delay(100)
                            binding.goodImage.startAnimation(scaleAnimation)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            binding.badImage.startAnimation(scaleAnimation)
                            binding.tooBadImage.startAnimation(scaleAnimation)
                            delay(500)
                            binding.goodImage.clearAnimation()
                            binding.sosoImage.clearAnimation()
                            binding.badImage.clearAnimation()
                            binding.tooBadImage.clearAnimation()
                            binding.goodImage.visibility = View.GONE
                            binding.sosoImage.visibility = View.GONE
                            binding.badImage.visibility = View.GONE
                            binding.tooBadImage.visibility = View.GONE
                            delay(100)
                            val translateAnimation = TranslateAnimation(
                                500f,
                                1f,
                                1f,
                                1f,
                            )
                            translateAnimation.duration = 500
                            val alphaAnimation = AlphaAnimation(
                                0.3f,
                                1f,
                            )
                            alphaAnimation.duration = 500
                            binding.score100.visibility = View.VISIBLE
                            binding.score100.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 500
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
//                            binding.feedBack.let {
//                                it.setImageResource(R.drawable.ic_perfect)
//                                it.setColorFilter(Color.parseColor("#4CAF50"))
//                                it.visibility = View.VISIBLE
//                            }
                        }
                    }
                }



                binding.goodImage.setOnClickListener {
                    if (!select) {
                        select = true
                        selectMark = "good"
                        lifecycleScope.launch(Dispatchers.Main) {
                            val scaleAnimation = ScaleAnimation(
                                1f,
                                0.3f,
                                1f,
                                0.3f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                                it.fillAfter = true
                            }
                            val translateAnimation2 = TranslateAnimation(
                                1f,
                                -200f,
                                1f,
                                1f
                            )
                            translateAnimation2.let {
                                it.duration = 500
                                it.fillAfter = true
                            }

                            binding.goodImage.alpha = 1F
                            binding.goodImage.setColorFilter(Color.parseColor("#CDDC39"))
                            binding.perfectImage.startAnimation(scaleAnimation)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            binding.badImage.startAnimation(scaleAnimation)
                            binding.tooBadImage.startAnimation(scaleAnimation)
                            delay(500)
                            binding.perfectImage.clearAnimation()
                            binding.sosoImage.clearAnimation()
                            binding.badImage.clearAnimation()
                            binding.tooBadImage.clearAnimation()
                            binding.perfectImage.visibility = View.GONE
                            binding.sosoImage.visibility = View.GONE
                            binding.badImage.visibility = View.GONE
                            binding.tooBadImage.visibility = View.GONE
                            delay(500)
                            binding.goodImage.startAnimation(translateAnimation2)
                            delay(100)
                            val translateAnimation = TranslateAnimation(
                                500f,
                                1f,
                                1f,
                                1f,
                            )
                            translateAnimation.duration = 500
                            binding.score80.visibility = View.VISIBLE
                            binding.score80.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 500
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
//                            binding.feedBack.let {
//                                it.setImageResource(R.drawable.ic_good)
//                                it.setColorFilter(Color.parseColor("#CDDC39"))
//                                it.visibility = View.VISIBLE
//                            }
                        }
                    }
                }




                binding.sosoImage.setOnClickListener {
                    if (!select) {
                        select = true
                        selectMark = "soso"
                        lifecycleScope.launch(Dispatchers.Main) {
                            val scaleAnimation = ScaleAnimation(
                                1f,
                                0.3f,
                                1f,
                                0.3f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                                it.fillAfter = true
                            }
                            val translateAnimation2 = TranslateAnimation(
                                1f,
                                -400f,
                                1f,
                                1f
                            )
                            translateAnimation2.let {
                                it.duration = 500
                                it.fillAfter = true
                            }

                            binding.sosoImage.alpha = 1F
                            binding.sosoImage.setColorFilter(Color.parseColor("#FFC107"))

                            binding.perfectImage.startAnimation(scaleAnimation)
                            binding.goodImage.startAnimation(scaleAnimation)
                            binding.badImage.startAnimation(scaleAnimation)
                            binding.tooBadImage.startAnimation(scaleAnimation)

                            delay(500)

                            binding.perfectImage.clearAnimation()
                            binding.goodImage.clearAnimation()
                            binding.badImage.clearAnimation()
                            binding.tooBadImage.clearAnimation()

                            binding.perfectImage.visibility = View.GONE
                            binding.goodImage.visibility = View.GONE
                            binding.badImage.visibility = View.GONE
                            binding.tooBadImage.visibility = View.GONE

                            delay(500)

                            binding.sosoImage.startAnimation(translateAnimation2)

                            delay(100)

                            val translateAnimation = TranslateAnimation(
                                500f,
                                1f,
                                1f,
                                1f,
                            )
                            translateAnimation.duration = 500
                            binding.score60.visibility = View.VISIBLE
                            binding.score60.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 500
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
//                            binding.feedBack.let {
//                                it.setImageResource(R.drawable.ic_soso)
//                                it.setColorFilter(Color.parseColor("#FFC107"))
//                                it.visibility = View.VISIBLE
                            }
                        }
                    }
                }


                binding.badImage.setOnClickListener {
                    if (!select) {
                        select = true
                        selectMark = "bad"
                        lifecycleScope.launch(Dispatchers.Main) {
                            val scaleAnimation = ScaleAnimation(
                                1f,
                                0.3f,
                                1f,
                                0.3f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                                it.fillAfter = true
                            }
                            val translateAnimation2 = TranslateAnimation(
                                1f,
                                -580f,
                                1f,
                                1f
                            )
                            translateAnimation2.let {
                                it.duration = 500
                                it.fillAfter = true
                            }

                            binding.badImage.alpha = 1F
                            binding.badImage.setColorFilter(Color.parseColor("#FF9800"))

                            binding.perfectImage.startAnimation(scaleAnimation)
                            binding.goodImage.startAnimation(scaleAnimation)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            binding.tooBadImage.startAnimation(scaleAnimation)

                            delay(500)

                            binding.perfectImage.clearAnimation()
                            binding.goodImage.clearAnimation()
                            binding.sosoImage.clearAnimation()
                            binding.tooBadImage.clearAnimation()

                            binding.perfectImage.visibility = View.GONE
                            binding.goodImage.visibility = View.GONE
                            binding.sosoImage.visibility = View.GONE
                            binding.tooBadImage.visibility = View.GONE

                            delay(500)

                            binding.badImage.startAnimation(translateAnimation2)

                            delay(100)

                            val translateAnimation = TranslateAnimation(
                                500f,
                                1f,
                                1f,
                                1f,
                            )
                            translateAnimation.duration = 500
                            binding.score40.visibility = View.VISIBLE
                            binding.score40.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 500
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
//                            binding.feedBack.let {
//                                it.setImageResource(R.drawable.ic_bad)
//                                it.setColorFilter(Color.parseColor("#FF9800"))
//                                it.visibility = View.VISIBLE
                            }
                        }
                    }
                }





                binding.tooBadImage.setOnClickListener {
                    if (!select) {
                        select = true
                        selectMark = "tooBad"
                        lifecycleScope.launch(Dispatchers.Main) {
                            val scaleAnimation = ScaleAnimation(
                                1f,
                                0.3f,
                                1f,
                                0.3f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                                it.fillAfter = true
                            }
                            val translateAnimation2 = TranslateAnimation(
                                1f,
                                -800f,
                                1f,
                                1f
                            )
                            translateAnimation2.let {
                                it.duration = 500
                                it.fillAfter = true
                            }

                            binding.tooBadImage.alpha = 1F
                            binding.tooBadImage.setColorFilter(Color.parseColor("#f44336"))

                            binding.perfectImage.startAnimation(scaleAnimation)
                            binding.goodImage.startAnimation(scaleAnimation)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            binding.badImage.startAnimation(scaleAnimation)

                            delay(500)

                            binding.perfectImage.clearAnimation()
                            binding.goodImage.clearAnimation()
                            binding.sosoImage.clearAnimation()
                            binding.badImage.clearAnimation()

                            binding.perfectImage.visibility = View.GONE
                            binding.goodImage.visibility = View.GONE
                            binding.sosoImage.visibility = View.GONE
                            binding.badImage.visibility = View.GONE

                            delay(500)

                            binding.tooBadImage.startAnimation(translateAnimation2)

                            delay(100)

                            val translateAnimation = TranslateAnimation(
                                500f,
                                1f,
                                1f,
                                1f,
                            )
                            translateAnimation.duration = 500
                            binding.score20.visibility = View.VISIBLE
                            binding.score20.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 500
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
//                            binding.feedBack.let {
//                                it.setImageResource(R.drawable.ic_sick)
//                                it.setColorFilter(Color.parseColor("#f44336"))
//                                it.visibility = View.VISIBLE
//                            }
                        }
                    }
                }


                    binding.cancel.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Log.d("selectMark", selectMark)
                            select = false
                            val rotateAnimation = RotateAnimation(
                                0f,
                                -90f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.duration = 100
                            rotateAnimation.fillAfter = true
                            binding.cancel.startAnimation(rotateAnimation)
                            delay(100)
                            binding.cancel.clearAnimation()
                            binding.cancel.visibility = View.GONE
                            when (selectMark) {

                                "perfect" -> {
                                    binding.perfectImage.alpha = 0.6F
                                    binding.perfectImage.setColorFilter(Color.parseColor("#757575"))
                                    binding.score100.visibility = View.GONE
                                    val scaleAnimation = ScaleAnimation(
                                        0.3f,
                                        1f,
                                        0.3f,
                                        1f,
                                        50f,
                                        50f
                                    )
                                    scaleAnimation.duration = 100
                                    binding.goodImage.visibility = View.VISIBLE
                                    binding.goodImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.sosoImage.visibility = View.VISIBLE
                                    binding.sosoImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.badImage.visibility = View.VISIBLE
                                    binding.badImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.tooBadImage.visibility = View.VISIBLE
                                    binding.tooBadImage.startAnimation(scaleAnimation)
//                                    binding.feedBack.visibility = View.INVISIBLE
                                }

                                "good" -> {
                                    binding.goodImage.alpha = 0.6F
                                    binding.goodImage.setColorFilter(Color.parseColor("#757575"))
                                    binding.score80.visibility = View.GONE
                                    val scaleAnimation = ScaleAnimation(
                                        0.3f,
                                        1f,
                                        0.3f,
                                        1f,
                                        50f,
                                        50f
                                    )
                                    val translateAnimation = TranslateAnimation(
                                        -200f,
                                        1f,
                                        1f,
                                        1f
                                    )
                                    translateAnimation.duration = 500
                                    scaleAnimation.duration = 100
                                    binding.goodImage.startAnimation(translateAnimation)
                                    binding.perfectImage.visibility = View.VISIBLE
                                    binding.perfectImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.sosoImage.visibility = View.VISIBLE
                                    binding.sosoImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.badImage.visibility = View.VISIBLE
                                    binding.badImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.tooBadImage.visibility = View.VISIBLE
                                    binding.tooBadImage.startAnimation(scaleAnimation)
//                                    binding.feedBack.visibility = View.INVISIBLE
                                }

                                "soso" -> {
                                    binding.sosoImage.alpha = 0.6F
                                    binding.sosoImage.setColorFilter(Color.parseColor("#757575"))
                                    binding.score60.visibility = View.GONE
                                    val scaleAnimation = ScaleAnimation(
                                        0.3f,
                                        1f,
                                        0.3f,
                                        1f,
                                        50f,
                                        50f
                                    )
                                    val translateAnimation = TranslateAnimation(
                                        -400f,
                                        1f,
                                        1f,
                                        1f
                                    )
                                    translateAnimation.duration = 500
                                    scaleAnimation.duration = 100
                                    binding.sosoImage.startAnimation(translateAnimation)
                                    binding.perfectImage.visibility = View.VISIBLE
                                    binding.perfectImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.goodImage.visibility = View.VISIBLE
                                    binding.goodImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.badImage.visibility = View.VISIBLE
                                    binding.badImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.tooBadImage.visibility = View.VISIBLE
                                    binding.tooBadImage.startAnimation(scaleAnimation)
//                                    binding.feedBack.visibility = View.INVISIBLE
                                }

                                "bad" -> {
                                    binding.badImage.alpha = 0.6F
                                    binding.badImage.setColorFilter(Color.parseColor("#757575"))
                                    binding.score40.visibility = View.GONE
                                    val scaleAnimation = ScaleAnimation(
                                        0.3f,
                                        1f,
                                        0.3f,
                                        1f,
                                        50f,
                                        50f
                                    )
                                    val translateAnimation = TranslateAnimation(
                                        -580f,
                                        1f,
                                        1f,
                                        1f
                                    )
                                    translateAnimation.duration = 500
                                    scaleAnimation.duration = 100
                                    binding.badImage.startAnimation(translateAnimation)
                                    binding.perfectImage.visibility = View.VISIBLE
                                    binding.perfectImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.goodImage.visibility = View.VISIBLE
                                    binding.goodImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.sosoImage.visibility = View.VISIBLE
                                    binding.sosoImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.tooBadImage.visibility = View.VISIBLE
                                    binding.tooBadImage.startAnimation(scaleAnimation)
//                                    binding.feedBack.visibility = View.INVISIBLE
                                }


                                "tooBad" -> {
                                    binding.tooBadImage.alpha = 0.6F
                                    binding.tooBadImage.setColorFilter(Color.parseColor("#757575"))
                                    binding.score20.visibility = View.GONE
                                    val scaleAnimation = ScaleAnimation(
                                        0.3f,
                                        1f,
                                        0.3f,
                                        1f,
                                        50f,
                                        50f
                                    )
                                    val translateAnimation = TranslateAnimation(
                                        -800f,
                                        1f,
                                        1f,
                                        1f
                                    )
                                    translateAnimation.duration = 500
                                    scaleAnimation.duration = 100
                                    binding.tooBadImage.startAnimation(translateAnimation)
                                    binding.perfectImage.visibility = View.VISIBLE
                                    binding.perfectImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.goodImage.visibility = View.VISIBLE
                                    binding.goodImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.sosoImage.visibility = View.VISIBLE
                                    binding.sosoImage.startAnimation(scaleAnimation)
                                    delay(200)
                                    binding.badImage.visibility = View.VISIBLE
                                    binding.badImage.startAnimation(scaleAnimation)
                                }
                            }
                        }
                    }

                val courseList = listOf<Int>(
                    R.drawable.ic_black,
                    R.drawable.ic_red,
                    R.drawable.ic_blue,
                    R.drawable.ic_green,
                    R.drawable.ic_pink,
                    R.drawable.ic_yellow,
                    R.drawable.ic_purple,
                    R.drawable.ic_brown,
                    R.drawable.ic_gray,
                    R.drawable.ic_return2
                )

                val adapter = MyAdapter(courseList,position)
                binding.mainRecyclerView.adapter = adapter
                binding.mainRecyclerView.layoutManager =  LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter.setOnItemClickListener(
                    object: MyAdapter.OnCourseListener {
                        override fun onItemClick(list: List<Int>,position: Int) {
                            when(courseList[position]) {
                                R.drawable.ic_black -> {
                                    val color = "#FF000000"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_red -> {
                                    val color = "#f44336"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_return2 -> {
                                    val color = "#FFFFFFFF"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_blue -> {
                                    val color = "#2196F3"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_green -> {
                                    val color = "#4CAF50"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_pink -> {
                                    val color = "#ff1493"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_yellow -> {
                                    val color = "#FFFF00"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_purple -> {
                                    val color = "#800080"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_brown -> {
                                    val color = "#795548"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }

                                R.drawable.ic_gray -> {
                                    val color = "#757575"
                                    binding.totalTime.setTextColor(Color.parseColor(color))
                                    binding.totalDistance.setTextColor(Color.parseColor(color))
                                    binding.totalCalorie.setTextColor(Color.parseColor(color))
                                    binding.today.setTextColor(Color.parseColor(color))
                                    binding.photoText.setTextColor(Color.parseColor(color))
                                }
                            }
                        }
                    })

                binding.resultButton.setOnClickListener {
                    memo = binding.memo.text.toString()
                    Log.d("memo", memo)
                }
            }

    private fun openCamera() {
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE,"New Picture")
        value.put(MediaStore.Images.Media.DESCRIPTION,"From Picture")
        image_uri = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,value)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_DENIED
                ) {
                    openCamera()
                } else {
                    Toast.makeText(requireContext(),"カメラが拒否されました",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            (data?.extras?.get("data") as? Bitmap).let {
                binding.photoEmpty.setImageBitmap(it)
                val width = binding.photoEmpty.width.toFloat()
                val height = binding.photoEmpty.height.toFloat()
                val rotateValue = arrayOf(90f,width,height)
                binding.photoEmpty.rotation = 90f

            }
        }
    }
//    private fun checkCameraPermission() = PackageManager.PERMISSION_GRANTED ==
//            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)
//
//    private fun takePicture() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        onActivityResult(requestCode, resultCode, data)
//        if (requestCode == CAMERA_REQUEST_CODE) {
//
//        }
//    }
}