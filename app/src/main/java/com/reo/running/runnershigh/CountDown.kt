package com.reo.running.runnershigh

import android.animation.Animator
import android.graphics.Canvas
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.*
import androidx.fragment.app.Fragment
import com.reo.running.runnershigh.databinding.CountDownBinding

class CountDown : Fragment() {

    private lateinit var binding: CountDownBinding
    private lateinit var animT:TranslateAnimation
    private lateinit var animS:ScaleAnimation
    private lateinit var animSet:AnimationSet
    private lateinit var a:Animation
    private lateinit var wm:WindowManager

    val handler = Handler()

    var cnt = 0

    private var shortAnimationDuration:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CountDownBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val w = (view.width / 2).toFloat()
        val h = (view.height / 2).toFloat()
        val scale = ScaleAnimation(0f,100f,0f,100f,w,h)
        scale.duration = 1000
        binding.countNum5.startAnimation(scale)
    }
}