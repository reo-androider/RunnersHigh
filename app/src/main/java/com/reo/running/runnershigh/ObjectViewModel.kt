package com.reo.running.runnershigh

import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.core.SnapshotHolder
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ObjectViewModel : ViewModel() {
    val dbObjective = Firebase.database.getReference("object")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val objective = snapshot.value
            }
            override fun onCancelled(error: DatabaseError) {}
        })
}