package com.reo.running.runnershigh

import android.animation.Animator
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.reo.running.runnershigh.databinding.CountDownBinding

class CountDown : Fragment() {

    private lateinit var binding: CountDownBinding
    var cnt = 5

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CountDownBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for (i in 1..6) {
            val scale = ScaleAnimation(
                    0f,
                    100f,
                    0f,
                    100f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f
            )
            scale.duration = 3000
            when (cnt) {
                5 -> binding.countNum5.startAnimation(scale)
                4 -> binding.countNum4.startAnimation(scale)
                3 -> binding.countNum3.startAnimation(scale)
                2 -> binding.countNum2.startAnimation(scale)
                1 -> binding.countNum1.startAnimation(scale)
                0 -> binding.countNum0.startAnimation(scale)
                -1 -> findNavController().navigate(R.id.navi_run)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            cnt += -1
        },3000)
    }
}

