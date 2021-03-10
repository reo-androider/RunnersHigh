package com.reo.running.runnershigh.fragments.run

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.JustRunData
import com.reo.running.runnershigh.JustRunDataDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.math.round

class RunViewModel(
    private val justRunDataDao: JustRunDataDao
) : ViewModel() {

    private val firebaseDB by lazy { Firebase.database }

    private val _distance = MutableLiveData<Float>()
    val distance: LiveData<Float>
        get() = _distance

    private val _weight = MutableLiveData<Double>()
    val weight: LiveData<Double> = _weight

    private val _runState = MutableLiveData(RunState.RUN_STATE_BEFORE)
    val runState: LiveData<RunState> = _runState

    val lastLocation = MutableLiveData<Location?>()
    private val previousLastLocation = MutableLiveData<Location>()

    private val totalMileage = MutableLiveData(0.0F)
    val roundedTotalMileage = totalMileage.map {
        round(it) / 1000
    }

    private val differenceMileage = MutableLiveData<FloatArray>()

    val totalConsumptionCalorie = combine(0, weight, roundedTotalMileage) { _, weight,  roundedTotalMileage->
        (roundedTotalMileage * weight).toInt()
    }

    val zoomValue = 18.0F

    val isTakenPhoto = MutableLiveData(false)

    init {
        _distance.value = 0f
        firebaseDB.getReference("weight").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString() != "") _weight.value =
                    snapshot.value.toString().toDouble()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun setRunState(runState: RunState) {
        _runState.value = runState
    }

    fun calcTotalMileage(location: Location?) {
        lastLocation.value = location
        lastLocation.value?.let { lastLocation ->
            previousLastLocation.value?.let { previousLastLocation ->
                Location.distanceBetween(
                    previousLastLocation.latitude,
                    previousLastLocation.longitude,
                    lastLocation.latitude,
                    lastLocation.longitude,
                    differenceMileage.value
                )
            }
        }
        previousLastLocation.value = lastLocation.value

        totalMileage.value = totalMileage.value?.plus(differenceMileage.value?.firstOrNull() ?: 0F)
    }

    fun saveRunData(stopWatchText: String, photo: Bitmap?, callback: () -> Unit) {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val record = JustRunData(
            0,
            stopWatchText,
            roundedTotalMileage.value ?: 0.0F,
            totalConsumptionCalorie.value ?: 0,
            current.format(formatter),
            photo,
            isTakenPhoto.value ?: false
        )

        viewModelScope.launch {
            justRunDataDao.insertRecord(record)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }


    companion object {
        class Factory(
            private val justRunDataDao: JustRunDataDao
        ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>) = RunViewModel(
                justRunDataDao
            ) as T
        }
    }
}

inline fun <T : Any, LIVE1 : Any, LIVE2 : Any> combine(
    initialValue: T,
    liveData1: LiveData<LIVE1>,
    liveData2: LiveData<LIVE2>,
    crossinline block: (T, LIVE1, LIVE2) -> T
): LiveData<T> {
    return MediatorLiveData<T>().apply {
        value = initialValue
        listOf(liveData1, liveData2).forEach { liveData ->
            addSource(liveData) {
                val currentValue = value
                val liveData1Value = liveData1.value
                val liveData2Value = liveData2.value
                if (currentValue != null && liveData1Value != null && liveData2Value != null) {
                    value = block(currentValue, liveData1Value, liveData2Value)
                }
            }
        }
    }.distinctUntilChanged()
}

enum class RunState {
    RUN_STATE_BEFORE,
    RUN_STATE_START,
    RUN_STATE_PAUSE
}