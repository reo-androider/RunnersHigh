package com.reo.running.runnershigh.fragments

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.errorprone.annotations.Var
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentProfileBinding
import java.util.jar.Manifest

class FragmentProfile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var input: Boolean = false
    private var myUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            settingImage.setOnClickListener {
                findNavController().navigate(R.id.action_navi_setting_to_fragmentProfileSetting)
            }

                val databaseRef = Firebase.database.getReference("user")
                databaseRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            val fireStore = snapshot.value
                            Log.d("Photo", "$fireStore")
                            myUri = Uri.parse(fireStore.toString())
                            profileImage.setImageURI(myUri)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }
}