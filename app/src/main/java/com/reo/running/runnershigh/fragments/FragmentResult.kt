package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.databinding.FragmentResultBinding
import kotlinx.coroutines.*

class FragmentResult : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val readDao = MyApplication.db.recordDao()
    private val runDB = MyApplication.db.recordDao2()
    private var select = false//二回押しても同じアニメーションが実行されない為
    private var selectMark = ""
    private var image_uri: Uri? = null
    private val contentResolver: ContentResolver? = null
    private var position = 0
    private var draft: String = ""
    private var selectColor = ""

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
            Log.d("delete", "${readDao.getAll().lastOrNull()}")
            readDao.getAll().lastOrNull()?.time?.let { Log.d("delete", it) }
            Log.d("delete", "${readDao.getAll().lastOrNull()?.distance}")
            Log.d("delete", "${readDao.getAll().lastOrNull()?.calorie}")
            val record = readDao.getAll()

            withContext(Dispatchers.Main) {
                binding.totalTime.text = record.lastOrNull()?.time
                binding.totalDistance.text = "${record.lastOrNull()?.distance}km"
                binding.totalCalorie.text = "${record.lastOrNull()?.calorie}kcal"
                binding.today.text = record.lastOrNull()?.runDate
                binding.photoEmpty.setImageBitmap(record.lastOrNull()?.bitmap)


                if (record.lastOrNull()?.takenPhoto == true) {
                    binding.cameraImage.visibility = View.GONE
                }

                binding.cameraImage.setOnClickListener {
                    binding.photoReturn.visibility = View.GONE
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
                binding.memo.movementMethod = ScrollingMovementMethod()

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

                val courseList = listOf(
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
                                    selectColor = "#FF000000"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_red -> {
                                    selectColor = "#f44336"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_return2 -> {
                                    selectColor = "#FFFFFFFF"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_blue -> {
                                    selectColor= "#2196F3"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_green -> {
                                    selectColor = "#4CAF50"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_pink -> {
                                    selectColor = "#ff1493"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_yellow -> {
                                    selectColor = "#FFFF00"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_purple -> {
                                    selectColor = "#800080"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_brown -> {
                                    selectColor = "#795548"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_gray -> {
                                    selectColor = "#757575"
                                    binding.totalTime.setTextColor(Color.parseColor(selectColor))
                                    binding.totalDistance.setTextColor(Color.parseColor(selectColor))
                                    binding.totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }
                            }
                        }
                    })

        binding.photoCancel.setOnClickListener {
            binding.photoCancel.visibility = View.GONE
            binding.cameraImage.visibility = View.VISIBLE
            binding.photoEmpty.visibility = View.GONE
            binding.photoReturn.visibility = View.VISIBLE
        }

        binding.photoReturn.setOnClickListener {
            binding.photoReturn.visibility = View.GONE
            binding.photoCancel.visibility = View.VISIBLE
            binding.photoEmpty.visibility = View.VISIBLE
            binding.cameraImage.visibility = View.GONE
        }

        binding.edit.setOnClickListener {
            val myEdit = EditText(requireContext())
            myEdit.isSingleLine = false
            myEdit.text = draft.toEditable()
            myEdit.width = 20
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("気づき・メモ")
            dialog.setCancelable(false)
            dialog.setView(myEdit)
            dialog.setPositiveButton("完了") { _, _->
                draft = myEdit.text.toString()
                binding.memo.text = draft

            }

            dialog.setNegativeButton("戻る") { _, _ ->

            }
            dialog.show()
        }

        binding.delete.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("メモを削除しますか？")
            dialog.setCancelable(false)
            dialog.setPositiveButton ("はい") { _, _ ->
                draft = ""
                binding.memo.text = resources.getString(R.string.memo)
            }
            dialog.setNegativeButton("いいえ") { _, _ ->
            }
            dialog.show()
        }

        binding.resultButton.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val record = readDao.getAll().lastOrNull()
                val record2 = record?.time?.let { it1 ->
                    Record2(
                        0,
                        record.bitmap,
                        it1,
                        record.distance,
                        record.calorie,
                        record.runDate,
                        selectColor,
                        selectMark,
                        draft
                    )
                }

                if (record2 != null) {
                    runDB.insertRecord2(record2)
                }

                Log.d("ROOM","${runDB.getAll2()}")


//                myRealm = Realm.getDefaultInstance()
//                myRealm.executeTransaction {
//                    var data = myRealm.createObject(ResultDataModel::class.java,id)
//                    data.memo = draft
//                    data.colorId = selectColor
//                    data.feedBack = selectMark
//                    myRealm.copyToRealm(data)
//                    Log.d("Realm","${myRealm.where(ResultDataModel::class.java).findAll()}")
//                }
//                myRealm = Realm.getDefaultInstance()
//                val resultData = ResultDataModel(draft,selectMark)
//                myRealm = Realm.getDefaultInstance()
//                myRealm.beginTransaction()
//                myRealm.insert(resultData)
//                myRealm.commitTransaction()
//                val record = readDao.getAll()
////                val data = runData(
////                    record.last().distance,
////                    record.last().time,
////                    record.last().calorie,
////                    record.last().runDate,
////                    selectMark,
////                    draft,
////                    selectColor
////                )
//                val databaseRef = Firebase.database.reference
////                databaseRef.setValue(data)
//                databaseRef.push()
////                Log.d("RealtimeDB","$data")
//                withContext(Dispatchers.Main) {
//
//                }
            }
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
                    Toast.makeText(requireContext(),"カメラ拒否されました",Toast.LENGTH_LONG).show()
                } else {
                    openCamera()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("photo","${data?.extras?.get("data")}")
        if (resultCode == Activity.RESULT_OK) {
            (data?.extras?.get("data") as? Bitmap)?.let { bitmap ->
                Log.d("photo","$bitmap")
                Matrix().apply {
                    postRotate(90f)
                }.run {
                    Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,this,true)
                }.run {
                    binding.photoEmpty.visibility = View.VISIBLE
                    binding.resultButton.visibility = View.GONE
                    Log.d("photo","$this")
                    binding.photoEmpty.setImageBitmap(this)
                    binding.cameraImage.visibility = View.GONE
                    binding.photoCancel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
//
//    override fun onResume() {
//        super.onResume()
//        mRealm = Realm.getDefaultInstance()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mRealm.close()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mRealm.close()
//    }
//
//    fun create(memo: String) {
//        mRealm.executeTransaction {
//            var book = mRealm.createObject(Memo::class.java,UUID.randomUUID().toString())
//            book.memo = memo
//            mRealm.copyToRealm(book)
//            Log.d("Realm","${book}")
//        }
//    }
//
//    fun read() : RealmResults<Memo> {
//        return mRealm.where(Memo::class.java).findAll()
//    }
//
//    fun update(id: String, memo: String) {
//        mRealm.executeTransaction {
//            var book = mRealm.where(Memo::class.java).equalTo("id",id).findFirst()
//            book?.memo = memo
//            if (memo != 0.toString()) {
//                book?.memo = memo
//            }
//        }
//    }
//
//    fun delete(id:String) {
//        mRealm.executeTransaction {
//            var book = mRealm.where(Memo::class.java).equalTo("id",id).findAll()
//            book.deleteFromRealm(0)
//        }
//    }
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