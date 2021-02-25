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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.reo.running.runnershigh.*
import com.reo.running.runnershigh.databinding.FragmentResultBinding
import kotlinx.coroutines.*

class ResultFragment : Fragment() {

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
        binding.run {
            lifecycleScope.launch(Dispatchers.IO) {
                val record = readDao.getAll()

                withContext(Dispatchers.Main) {
                    totalTime.text = record.lastOrNull()?.time
                    totalDistance.text = "${record.lastOrNull()?.distance}km"
                    totalCalorie.text = "${record.lastOrNull()?.calorie}kcal"
                    today.text = record.lastOrNull()?.runDate
                    photoEmpty.setImageBitmap(record.lastOrNull()?.bitmap)

                    if (record.lastOrNull()?.takenPhoto == true) {
                        cameraImage.visibility = View.GONE
                    }

                    cameraImage.setOnClickListener {
                        photoReturn.visibility = View.GONE
                        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_DENIED ||
                                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

                    perfectImage.alpha = 0.6F
                    goodImage.alpha = 0.6F
                    sosoImage.alpha = 0.6F
                    badImage.alpha = 0.6F
                    tooBadImage.alpha = 0.6F
                    memo.movementMethod = ScrollingMovementMethod()

                    lifecycleScope.launch(Dispatchers.Main) {
                        perfectImage.setOnClickListener {
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

                                    perfectImage.alpha = 1F
                                    perfectImage.setColorFilter(Color.parseColor("#4CAF50"))
                                    delay(100)
                                    goodImage.startAnimation(scaleAnimation)
                                    sosoImage.startAnimation(scaleAnimation)
                                    badImage.startAnimation(scaleAnimation)
                                    tooBadImage.startAnimation(scaleAnimation)
                                    delay(500)
                                    goodImage.clearAnimation()
                                    sosoImage.clearAnimation()
                                    badImage.clearAnimation()
                                    tooBadImage.clearAnimation()
                                    goodImage.visibility = View.GONE
                                    sosoImage.visibility = View.GONE
                                    badImage.visibility = View.GONE
                                    tooBadImage.visibility = View.GONE
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
                                    score100.visibility = View.VISIBLE
                                    score100.startAnimation(translateAnimation)
                                    val rotateAnimation = RotateAnimation(
                                            -90f,
                                            0f,
                                            0.5f,
                                            1f
                                    )
                                    rotateAnimation.duration = 500
                                    cancel.visibility = View.VISIBLE
                                    cancel.startAnimation(rotateAnimation)
                                }
                            }
                        }

                        goodImage.setOnClickListener {
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

                                    goodImage.alpha = 1F
                                    goodImage.setColorFilter(Color.parseColor("#CDDC39"))
                                    perfectImage.startAnimation(scaleAnimation)
                                    sosoImage.startAnimation(scaleAnimation)
                                    badImage.startAnimation(scaleAnimation)
                                    tooBadImage.startAnimation(scaleAnimation)
                                    delay(500)
                                    perfectImage.clearAnimation()
                                    sosoImage.clearAnimation()
                                    badImage.clearAnimation()
                                    tooBadImage.clearAnimation()
                                    perfectImage.visibility = View.GONE
                                    sosoImage.visibility = View.GONE
                                    badImage.visibility = View.GONE
                                    tooBadImage.visibility = View.GONE
                                    delay(500)
                                    goodImage.startAnimation(translateAnimation2)
                                    delay(100)
                                    val translateAnimation = TranslateAnimation(
                                            500f,
                                            1f,
                                            1f,
                                            1f,
                                    )
                                    translateAnimation.duration = 500
                                    score80.visibility = View.VISIBLE
                                    score80.startAnimation(translateAnimation)
                                    val rotateAnimation = RotateAnimation(
                                            -90f,
                                            0f,
                                            0.5f,
                                            1f
                                    )
                                    rotateAnimation.duration = 500
                                    cancel.visibility = View.VISIBLE
                                    cancel.startAnimation(rotateAnimation)
                                }
                            }
                        }

                        sosoImage.setOnClickListener {
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

                                    sosoImage.alpha = 1F
                                    sosoImage.setColorFilter(Color.parseColor("#FFC107"))

                                    perfectImage.startAnimation(scaleAnimation)
                                    goodImage.startAnimation(scaleAnimation)
                                    badImage.startAnimation(scaleAnimation)
                                    tooBadImage.startAnimation(scaleAnimation)

                                    delay(500)

                                    perfectImage.clearAnimation()
                                    goodImage.clearAnimation()
                                    badImage.clearAnimation()
                                    tooBadImage.clearAnimation()

                                    perfectImage.visibility = View.GONE
                                    goodImage.visibility = View.GONE
                                    badImage.visibility = View.GONE
                                    tooBadImage.visibility = View.GONE

                                    delay(500)

                                    sosoImage.startAnimation(translateAnimation2)

                                    delay(100)

                                    val translateAnimation = TranslateAnimation(
                                            500f,
                                            1f,
                                            1f,
                                            1f,
                                    )
                                    translateAnimation.duration = 500
                                    score60.visibility = View.VISIBLE
                                    score60.startAnimation(translateAnimation)
                                    val rotateAnimation = RotateAnimation(
                                            -90f,
                                            0f,
                                            0.5f,
                                            1f
                                    )
                                    rotateAnimation.duration = 500
                                    cancel.visibility = View.VISIBLE
                                    cancel.startAnimation(rotateAnimation)
                                }
                            }
                        }

                        badImage.setOnClickListener {
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

                                    badImage.alpha = 1F
                                    badImage.setColorFilter(Color.parseColor("#FF9800"))

                                    perfectImage.startAnimation(scaleAnimation)
                                    goodImage.startAnimation(scaleAnimation)
                                    sosoImage.startAnimation(scaleAnimation)
                                    tooBadImage.startAnimation(scaleAnimation)

                                    delay(500)

                                    perfectImage.clearAnimation()
                                    goodImage.clearAnimation()
                                    sosoImage.clearAnimation()
                                    tooBadImage.clearAnimation()

                                    perfectImage.visibility = View.GONE
                                    goodImage.visibility = View.GONE
                                    sosoImage.visibility = View.GONE
                                    tooBadImage.visibility = View.GONE

                                    delay(500)

                                    badImage.startAnimation(translateAnimation2)

                                    delay(100)

                                    val translateAnimation = TranslateAnimation(
                                            500f,
                                            1f,
                                            1f,
                                            1f,
                                    )
                                    translateAnimation.duration = 500
                                    score40.visibility = View.VISIBLE
                                    score40.startAnimation(translateAnimation)
                                    val rotateAnimation = RotateAnimation(
                                            -90f,
                                            0f,
                                            0.5f,
                                            1f
                                    )
                                    rotateAnimation.duration = 500
                                    cancel.visibility = View.VISIBLE
                                    cancel.startAnimation(rotateAnimation)
                                }
                            }
                        }
                    }

                    tooBadImage.setOnClickListener {
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

                                tooBadImage.alpha = 1F
                                tooBadImage.setColorFilter(Color.parseColor("#f44336"))

                                perfectImage.startAnimation(scaleAnimation)
                                goodImage.startAnimation(scaleAnimation)
                                sosoImage.startAnimation(scaleAnimation)
                                badImage.startAnimation(scaleAnimation)

                                delay(500)

                                perfectImage.clearAnimation()
                                goodImage.clearAnimation()
                                sosoImage.clearAnimation()
                                badImage.clearAnimation()

                                perfectImage.visibility = View.GONE
                                goodImage.visibility = View.GONE
                                sosoImage.visibility = View.GONE
                                badImage.visibility = View.GONE

                                delay(500)

                                tooBadImage.startAnimation(translateAnimation2)

                                delay(100)

                                val translateAnimation = TranslateAnimation(
                                        500f,
                                        1f,
                                        1f,
                                        1f,
                                )
                                translateAnimation.duration = 500
                                score20.visibility = View.VISIBLE
                                score20.startAnimation(translateAnimation)
                                val rotateAnimation = RotateAnimation(
                                        -90f,
                                        0f,
                                        0.5f,
                                        1f
                                )
                                rotateAnimation.duration = 500
                                cancel.visibility = View.VISIBLE
                                cancel.startAnimation(rotateAnimation)
                            }
                        }
                    }
                }
            }


            cancel.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    select = false
                    val rotateAnimation = RotateAnimation(
                            0f,
                            -90f,
                            0.5f,
                            1f
                    )
                    rotateAnimation.duration = 100
                    rotateAnimation.fillAfter = true
                    cancel.startAnimation(rotateAnimation)
                    delay(100)
                    cancel.clearAnimation()
                    cancel.visibility = View.GONE
                    when (selectMark) {
                        "perfect" -> {
                            perfectImage.alpha = 0.6F
                            perfectImage.setColorFilter(Color.parseColor("#757575"))
                            score100.visibility = View.GONE
                            val scaleAnimation = ScaleAnimation(
                                    0.3f,
                                    1f,
                                    0.3f,
                                    1f,
                                    50f,
                                    50f
                            )
                            scaleAnimation.duration = 100
                            goodImage.visibility = View.VISIBLE
                            goodImage.startAnimation(scaleAnimation)
                            delay(200)
                            sosoImage.visibility = View.VISIBLE
                            sosoImage.startAnimation(scaleAnimation)
                            delay(200)
                            badImage.visibility = View.VISIBLE
                            badImage.startAnimation(scaleAnimation)
                            delay(200)
                            tooBadImage.visibility = View.VISIBLE
                            tooBadImage.startAnimation(scaleAnimation)
                        }

                        "good" -> {
                            goodImage.alpha = 0.6F
                            goodImage.setColorFilter(Color.parseColor("#757575"))
                            score80.visibility = View.GONE
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
                            goodImage.startAnimation(translateAnimation)
                            perfectImage.visibility = View.VISIBLE
                            perfectImage.startAnimation(scaleAnimation)
                            delay(200)
                            sosoImage.visibility = View.VISIBLE
                            sosoImage.startAnimation(scaleAnimation)
                            delay(200)
                            badImage.visibility = View.VISIBLE
                            badImage.startAnimation(scaleAnimation)
                            delay(200)
                            tooBadImage.visibility = View.VISIBLE
                            tooBadImage.startAnimation(scaleAnimation)
                        }

                        "soso" -> {
                            sosoImage.alpha = 0.6F
                            sosoImage.setColorFilter(Color.parseColor("#757575"))
                            score60.visibility = View.GONE
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
                            sosoImage.startAnimation(translateAnimation)
                            perfectImage.visibility = View.VISIBLE
                            perfectImage.startAnimation(scaleAnimation)
                            delay(200)
                            goodImage.visibility = View.VISIBLE
                            goodImage.startAnimation(scaleAnimation)
                            delay(200)
                            badImage.visibility = View.VISIBLE
                            badImage.startAnimation(scaleAnimation)
                            delay(200)
                            tooBadImage.visibility = View.VISIBLE
                            tooBadImage.startAnimation(scaleAnimation)
                        }

                        "bad" -> {
                            badImage.alpha = 0.6F
                            badImage.setColorFilter(Color.parseColor("#757575"))
                            score40.visibility = View.GONE
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
                            badImage.startAnimation(translateAnimation)
                            perfectImage.visibility = View.VISIBLE
                            perfectImage.startAnimation(scaleAnimation)
                            delay(200)
                            goodImage.visibility = View.VISIBLE
                            goodImage.startAnimation(scaleAnimation)
                            delay(200)
                            sosoImage.visibility = View.VISIBLE
                            sosoImage.startAnimation(scaleAnimation)
                            delay(200)
                            tooBadImage.visibility = View.VISIBLE
                            tooBadImage.startAnimation(scaleAnimation)
                        }


                        "tooBad" -> {
                            tooBadImage.alpha = 0.6F
                            tooBadImage.setColorFilter(Color.parseColor("#757575"))
                            score20.visibility = View.GONE
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
                            tooBadImage.startAnimation(translateAnimation)
                            perfectImage.visibility = View.VISIBLE
                            perfectImage.startAnimation(scaleAnimation)
                            delay(200)
                            goodImage.visibility = View.VISIBLE
                            goodImage.startAnimation(scaleAnimation)
                            delay(200)
                            sosoImage.visibility = View.VISIBLE
                            binding.sosoImage.startAnimation(scaleAnimation)
                            delay(200)
                            badImage.visibility = View.VISIBLE
                            badImage.startAnimation(scaleAnimation)
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

            val adapter = PaintsAdapter(courseList, position)
            mainRecyclerView.adapter = adapter
            mainRecyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter.setOnItemClickListener(
                    object : PaintsAdapter.OnCourseListener {
                        override fun onItemClick(list: List<Int>, position: Int) {
                            when (courseList[position]) {
                                R.drawable.ic_black -> {
                                    selectColor = "#FF000000"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_red -> {
                                    selectColor = "#f44336"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_return2 -> {
                                    selectColor = "#FFFFFFFF"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_blue -> {
                                    selectColor = "#2196F3"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    binding.today.setTextColor(Color.parseColor(selectColor))
                                    binding.photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_green -> {
                                    selectColor = "#4CAF50"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_pink -> {
                                    selectColor = "#ff1493"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_yellow -> {
                                    selectColor = "#FFFF00"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_purple -> {
                                    selectColor = "#800080"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_brown -> {
                                    selectColor = "#795548"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }

                                R.drawable.ic_gray -> {
                                    selectColor = "#757575"
                                    totalTime.setTextColor(Color.parseColor(selectColor))
                                    totalDistance.setTextColor(Color.parseColor(selectColor))
                                    totalCalorie.setTextColor(Color.parseColor(selectColor))
                                    today.setTextColor(Color.parseColor(selectColor))
                                    photoText.setTextColor(Color.parseColor(selectColor))
                                }
                            }
                        }
                    })

            photoCancel.setOnClickListener {
                photoCancel.visibility = View.GONE
                cameraImage.visibility = View.VISIBLE
                photoEmpty.visibility = View.GONE
                photoReturn.visibility = View.VISIBLE
            }

            photoReturn.setOnClickListener {
                photoReturn.visibility = View.GONE
                photoCancel.visibility = View.VISIBLE
                photoEmpty.visibility = View.VISIBLE
                cameraImage.visibility = View.GONE
            }

            edit.setOnClickListener {
                val myEdit = EditText(requireContext())
                myEdit.run {
                    isSingleLine = false
                    text = draft.toEditable()
                    width = 20
                }
                val dialog = AlertDialog.Builder(requireContext())
                dialog.run {
                    setTitle("メモ欄").setCancelable(true)
                    setView(myEdit)
                    setPositiveButton("完了") { _, _ ->
                        draft = myEdit.text.toString()
                        memo.text = draft
                    }
//                    setNegativeButton("戻る") { _, _ ->
//                    }
                    show()
                }
            }

            delete.setOnClickListener {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.run {
                    setTitle("メモを削除しますか？").setCancelable(true)
                    setPositiveButton("はい") { _, _ ->
                        draft = ""
                        memo.text = resources.getString(R.string.memo)
                    }
                    show()
                }
            }

            resultButton.setOnClickListener {
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
                    withContext(Dispatchers.Main) {
                        findNavController().navigate(R.id.action_fragmentResult_to_navi_graph)
                    }
                }
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
        if (resultCode == Activity.RESULT_OK) {
            (data?.extras?.get("data") as? Bitmap)?.let { bitmap ->
                Matrix().apply {
                    postRotate(90f)
                }.run {
                    Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,this,true)
                }.run {
                    binding.photoEmpty.visibility = View.VISIBLE
                    binding.photoEmpty.setImageBitmap(this)
                    binding.cameraImage.visibility = View.GONE
                    binding.photoCancel.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}