package com.reo.running.runnershigh.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentProfileBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class FragmentProfile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    companion object {
        private const val RC_SIGN_IN = 123
    }
    private var login = ""
    private val databaseReferenceLogin = Firebase.database.getReference("Login")
    private val databaseReferenceLoginDay = Firebase.database.getReference("LoginDay")

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
            databaseReferenceLogin.setValue(login)
            databaseReferenceLogin.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val loginStatus = snapshot.value.toString()
                    if (loginStatus == "true") {
                        loginImage.visibility = View.GONE
                        logoutImage.visibility = View.VISIBLE
                        loginText.visibility = View.VISIBLE
                        databaseReferenceLoginDay.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val day = snapshot.value.toString()
                                loginDay.text = day
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            val databaseRefPhoto = Firebase.database.getReference("photo")
            databaseRefPhoto.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {
                        val fireStore = snapshot.value
                        // TODO
//                            profileImage.setImageURI(Uri.parse(fireStore.toString()))
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefFirstName = Firebase.database.getReference("firstName")
            databaseRefFirstName.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value
                    profileFirstName.text = "$fireStore"
                    profileFirstName.setTextColor(resources.getColor(R.color.normal2))
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefFamily = Firebase.database.getReference("familyName")
            databaseRefFamily.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value
                    profileFamilyName.text = "$fireStore"
                }

                override fun onCancelled(error: DatabaseError) {}

            })
            val databaseRefObjective = Firebase.database.getReference("objective")
            databaseRefObjective.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value
                    objectiveText.text = "$fireStore"
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            settingImage.setOnClickListener {
                findNavController().navigate(R.id.action_navi_setting_to_fragmentProfileSetting)
            }

            loginImage.setOnClickListener {
                databaseReferenceLogin.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            signOut()
                            createSignInIntent()
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            logoutImage.setOnClickListener {
                AuthUI.getInstance()
                    .delete(requireContext())
            }
        }
    }

    private fun createSignInIntent() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.TwitterBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnCompleteListener {
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                binding.loginText.visibility = View.VISIBLE
                val day = loginDate().toString()
                databaseReferenceLoginDay.setValue(day)
                binding.loginDay.text = day
                binding.loginImage.visibility = View.GONE
                binding.logoutImage.visibility = View.VISIBLE
                login = "true"
                databaseReferenceLogin.setValue(login)
                val user = FirebaseAuth.getInstance().currentUser
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loginDate(): String? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        return formatted
    }
}