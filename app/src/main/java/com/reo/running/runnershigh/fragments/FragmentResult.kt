package com.reo.running.runnershigh.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.scaleMatrix
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.facebook.FacebookServiceException
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentResultBinding
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class FragmentResult : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val readDao = MyApplication.db.recordDao()
    private var memo = ""
    var select = false  //二回押しても同じアニメーションが実行されない為
    var selectMark = ""

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
            binding.totalTime.text = "${readDao.getAll().last().time}"
            binding.totalDistance.text = "${readDao.getAll().last().distance}km"
            binding.totalCalorie.text = "${readDao.getAll().last().calorie}kcal"
            binding.today.text = "${readDao.getAll().last().runDate}"

            withContext(Dispatchers.Main) {
                binding.perfectImage.setOnClickListener {
                    if (select == false) {
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

                            binding.perfectImage.setColorFilter(Color.parseColor("#f44336"))
                            delay(100)
                            binding.goodImage.startAnimation(scaleAnimation)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            binding.badImage.startAnimation(scaleAnimation)
                            binding.tooBadImage.startAnimation(scaleAnimation)
                            delay(100)
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
                            translateAnimation.let {
                                it.duration = 500
                            }
                            val alphaAnimation = AlphaAnimation(
                                0.3f,
                                1f,
                            )
                            alphaAnimation.let {
                                it.duration = 500
                            }
                            binding.score100.visibility = View.VISIBLE
                            binding.score100.startAnimation(translateAnimation)
                            val rotateAnimation = RotateAnimation(
                                -90f,
                                0f,
                                0.5f,
                                1f
                            )
                            rotateAnimation.let {
                                it.duration = 500
                            }
                            binding.cancel.visibility = View.VISIBLE
                            binding.cancel.startAnimation(rotateAnimation)
                        }
                    }
                }

//                binding.goodImage
            }

            binding.cancel.setOnClickListener {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.perfectImage.setColorFilter(Color.parseColor("#757575"))
                    binding.score100.visibility = View.GONE
                    Log.d("selectMark", "$selectMark")
                    select = false
                    val rotateAnimation = RotateAnimation(
                        0f,
                        -90f,
                        0.5f,
                        1f
                    )
                    rotateAnimation.let {
                        it.duration = 500
                    }
                    binding.cancel.startAnimation(rotateAnimation)
                    delay(500)
                    binding.cancel.visibility = View.GONE
                    when (selectMark) {
                        "perfect" -> {
                            binding.goodImage.visibility = View.VISIBLE
                            binding.sosoImage.visibility = View.VISIBLE
                            binding.badImage.visibility = View.VISIBLE
                            binding.tooBadImage.visibility = View.VISIBLE
                            val scaleAnimation = ScaleAnimation(
                                0.3f,
                                1f,
                                0.3f,
                                1f,
                                50f,
                                50f
                            )
                            scaleAnimation.let {
                                it.duration = 500
                            }
                            binding.goodImage.startAnimation(scaleAnimation)
                            delay(300)
                            binding.sosoImage.startAnimation(scaleAnimation)
                            delay(300)
                            binding.badImage.startAnimation(scaleAnimation)
                            delay(300)
                            binding.tooBadImage.startAnimation(scaleAnimation)
                        }
                    }
                }
            }
            binding.resultButton.setOnClickListener {
                memo = binding.memo.text.toString()
                Log.d("memo", "$memo")
            }
        }
    }
}
