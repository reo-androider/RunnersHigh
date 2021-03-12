package com.reo.running.runnershigh.fragments.common

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment

fun scaleUpAnimation(
    operation: (ScaleAnimation) -> Unit = {},
    animationStartOperation: () -> Unit = {},
    animationEndOperation: () -> Unit = {},
    animationRepeatOperation: () -> Unit = {}
): ScaleAnimation =
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
        duration = 300
        fillAfter = true
        operation(this)
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = animationStartOperation()
            override fun onAnimationEnd(animation: Animation?) = animationEndOperation()
            override fun onAnimationRepeat(animation: Animation?) = animationRepeatOperation()
        })
    }

fun scaleUpAnimationMore(
    operation: (ScaleAnimation) -> Unit = {},
    animationStartOperation: () -> Unit = {},
    animationEndOperation: () -> Unit = {},
    animationRepeatOperation: () -> Unit = {}
): ScaleAnimation =
    ScaleAnimation(
        1f,
        100f,
        1f,
        100f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    ).apply {
        duration = 1500
        fillAfter = true
        operation(this)
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = animationStartOperation()
            override fun onAnimationEnd(animation: Animation?) = animationEndOperation()
            override fun onAnimationRepeat(animation: Animation?) = animationRepeatOperation()
        })
    }

fun scaleDownAnimation(
    operation: (ScaleAnimation) -> Unit = {},
    animationStartOperation: () -> Unit = {},
    animationEndOperation: () -> Unit = {},
    animationRepeatOperation: () -> Unit = {}
): ScaleAnimation =
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
        duration = 300
        fillAfter = true
        operation(this)
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = animationStartOperation()
            override fun onAnimationEnd(animation: Animation?) = animationEndOperation()
            override fun onAnimationRepeat(animation: Animation?) = animationRepeatOperation()
        })
    }

fun animationCount(view: View) {
    view.startAnimation(
        ScaleAnimation(
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

@RequiresApi(Build.VERSION_CODES.O)
fun Fragment.vibratorOn(vibratorType: VibrationType) {
    val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    when (vibratorType) {
        VibrationType.LONG_VIBRATION -> {
            val vibrationEffect = VibrationEffect.createOneShot(800, 255)
            vibrator.vibrate(vibrationEffect)
        }
        VibrationType.MIDDLE_VIBRATION -> {
            val vibrationEffect = VibrationEffect.createOneShot(600, 255)
            vibrator.vibrate(vibrationEffect)
        }
        VibrationType.SHORT_VIBRATION -> {
            val vibrationEffect = VibrationEffect.createOneShot(300, 255)
            vibrator.vibrate(vibrationEffect)
        }
    }
}

enum class VibrationType() {
    LONG_VIBRATION,
    MIDDLE_VIBRATION,
    SHORT_VIBRATION
}