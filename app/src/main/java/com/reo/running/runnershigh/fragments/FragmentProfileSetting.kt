package com.reo.running.runnershigh.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentProfileSettingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FragmentProfileSetting : Fragment() {

    private lateinit var binding:FragmentProfileSettingBinding
    private lateinit var database:DatabaseReference
    private var profilePhoto:Bitmap? = null
    private var firstName:String? = null
    private var familyName:String? = null
    private var objective:String? = null
    private var weight:Float? = null

    companion object {
        val PERMISSION_CODE = 1
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            firstName = editFirstName.text.toString()
            familyName = editFamilyName.text.toString()
            objective = editObjective.text.toString()
            val profileData = ProfileData(profilePhoto,firstName,familyName,objective,weight)

            profileBack.setOnClickListener {
                val databaseRef = Firebase.database.reference
                databaseRef.setValue(profileData)
                findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
            }

            profileImage.setOnClickListener {
                if (checkSelfPermission(requireContext(),Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    requestPermissions(permission, PERMISSION_CODE)

                } else {
                    openCamera()
                }
            }

        }
    }

    private fun openCamera() {}
}