package com.reo.running.runnershigh.fragments.profile.photo

import androidx.lifecycle.*
import com.reo.running.runnershigh.RunResult
import com.reo.running.runnershigh.RunResultDao
import kotlinx.coroutines.launch

class PhotoListViewModel (
        private val runResultDao: RunResultDao
) : ViewModel() {

    private val _runResultList: MutableLiveData<List<RunResult>> = MutableLiveData()
    val runResultList: LiveData<List<RunResult>> = _runResultList

    init {
        viewModelScope.launch {
            _runResultList.value = runResultDao.getAll().reversed()
        }
    }

    companion object {
        class Factory(
                private val runResultDao: RunResultDao
                ) : ViewModelProvider.NewInstanceFactory() {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>) = PhotoListViewModel(
                    runResultDao
            ) as T
        }
    }
}