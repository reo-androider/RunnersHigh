package com.reo.running.runnershigh.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.databinding.FragmentResultBinding
import kotlinx.coroutines.*
import okhttp3.Dispatcher

class FragmentResult : Fragment() {

    private lateinit var binding: FragmentResultBinding
    private val readDao = MyApplication.db.recordDao()
    private var memo = ""

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
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.perfectImage.setColorFilter(Color.parseColor("#f44336"))
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

                        delay(1000)
                        binding.goodImage.startAnimation(scaleAnimation)
                        binding.sosoImage.startAnimation(scaleAnimation)
                        binding.badImage.startAnimation(scaleAnimation)
                        binding.tooBadImage.startAnimation(scaleAnimation)
                        delay(1000)
                        binding.goodImage.clearAnimation()
                        binding.sosoImage.clearAnimation()
                        binding.badImage.clearAnimation()
                        binding.tooBadImage.clearAnimation()
                        binding.goodImage.visibility = View.GONE
                        binding.sosoImage.visibility = View.GONE
                        binding.badImage.visibility = View.GONE
                        binding.tooBadImage.visibility = View.GONE
                        binding.cancel.visibility = View.VISIBLE
                    }
                }
            }

//
//                binding.cancel.setOnClickListener {
//                    binding.cancel.visibility = View.GONE
//                    val scaleAnimation = ScaleAnimation(
//                        0.2f,
//                        1f,
//                        0.2f,
//                        1f
//                    )
//                    scaleAnimation.let {
//                        it.duration = 500
//                    }
//                    binding.goodImage.visibility = View.VISIBLE
//                    binding.sosoImage.visibility = View.VISIBLE
//                    binding.badImage.visibility = View.VISIBLE
//                    binding.tooBadImage.visibility = View.VISIBLE
//                    binding.cancel.visibility = View.GONE
//                    binding.goodImage.startAnimation(scaleAnimation)
//                    binding.sosoImage.startAnimation(scaleAnimation)
//                    binding.badImage.startAnimation(scaleAnimation)
//                    binding.tooBadImage.startAnimation(scaleAnimation)
//
//                }
//
//                binding.resultButton.setOnClickListener {
//                    memo = binding.memo.text.toString()
//                    Log.d("memo", "$memo")
//
//                }
//            }
        }
    }
}