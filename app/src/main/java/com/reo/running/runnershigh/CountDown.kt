package com.reo.running.runnershigh

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.fragment.app.Fragment
import com.reo.running.runnershigh.databinding.CountDownBinding

class CountDown : Fragment() {

    private lateinit var binding: CountDownBinding
    private lateinit var animT:TranslateAnimation
    private lateinit var animS:ScaleAnimation
    private lateinit var animSet:AnimationSet
    private lateinit var a:Animation

    var cnt = 0

    private var shortAnimationDuration:Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = CountDownBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale = ScaleAnimation(10f,100f,10f,100f)
        scale.duration = 10000

    }
}