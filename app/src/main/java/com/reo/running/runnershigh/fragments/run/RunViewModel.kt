package com.reo.running.runnershigh.fragments.run

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RunViewModel : ViewModel() {
    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float>
        get() = _distance
    init {
        _distance.value = 0f
    }
}