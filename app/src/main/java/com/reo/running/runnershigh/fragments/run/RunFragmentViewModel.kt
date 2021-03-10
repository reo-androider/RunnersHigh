package com.reo.running.runnershigh.fragments.run

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.ceil

class RunFragmentViewModel : ViewModel() {
    /**
     * todo runStateからViewModelに移そうと思ったが、難しそうだったので保留
     *
     *     companion object {
     *     private const val RUN_STATE_BEFORE = 3001
     *     private const val RUN_STATE_START = 3002
     *     private const val RUN_STATE_PAUSE = 3003
     *     }
     *     private val _runState = MutableLiveData<Int>()
     *     val runState: LiveData<Int>
     *         get() = _runState
     */

    private val _kmAmount = MutableLiveData<Float>()
    val kmAmount: LiveData<Float>
        get() = _kmAmount

    init {
        _kmAmount.value = 0.0f
    }

    fun roundUp() {
        _kmAmount.value = _kmAmount.value?.let {
            ceil(it * 1000) / 1000
        }
    }
}