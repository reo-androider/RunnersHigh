package com.reo.running.runnershigh.fragments.run

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RunFragmentViewModel : ViewModel() {

    companion object {
        private const val RUN_STATE_BEFORE = 3001
        private const val RUN_STATE_START = 3002
        private const val RUN_STATE_PAUSE = 3003
    }

    private val _runState = MutableLiveData<Int>()
    val runState: LiveData<Int>
    get() = _runState

    init {
        _runState.value = RUN_STATE_BEFORE
    }


}