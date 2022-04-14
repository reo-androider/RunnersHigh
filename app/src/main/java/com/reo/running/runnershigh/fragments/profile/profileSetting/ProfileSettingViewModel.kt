package com.reo.running.runnershigh.fragments.profile.profileSetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileSettingViewModel(
) : ViewModel() {
    private val _lastName = MutableLiveData("")
    val lastName: LiveData<String> = _lastName

    private val _firstName = MutableLiveData("")
    val firstName: LiveData<String> = _firstName

    private val _objective = MutableLiveData("")
    val objective: LiveData<String> = _objective

    private val _weight = MutableLiveData("")
    val weight: LiveData<String> = _weight

    fun onLastNameChange(newLastName: String) {
        _lastName.value = newLastName
    }

    fun onFirstNameChange(newFirstName: String) {
        _firstName.value = newFirstName
    }

    fun onObjectiveChange(newObjective: String) {
        _objective.value = newObjective
    }

    fun onWeightChange(newWeight: String) {
        _weight.value = newWeight
    }

    fun saveProfileSetting() {
        val databaseReference = Firebase.database
        with(databaseReference) {
            getReference("familyName").setValue(lastName.value)
            getReference("firstName").setValue(firstName.value)
            getReference("objective").setValue(objective.value)
            getReference("weight").setValue(weight.value)
        }
    }
}
