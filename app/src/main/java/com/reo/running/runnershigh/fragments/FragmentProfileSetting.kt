package com.reo.running.runnershigh.fragments

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.internal.InternalTokenProvider
import com.google.firebase.ktx.Firebase
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.Resource
import com.reo.running.runnershigh.databinding.FragmentProfileSettingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.URI

class FragmentProfileSetting : Fragment() {

    private lateinit var binding:FragmentProfileSettingBinding
    private lateinit var database:DatabaseReference
    private var profilePhoto:Bitmap? = null
    private val readDao = MyApplication.db.recordDao()
    private var takePhoto:Bitmap? = null
    private var contentResolver: ContentResolver? = null
    var uri:String = ""

    companion object {
        val PERMISSION_CODE = 1
        val READ_REQUEST_CODE = 2
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d("ROOM","${readDao.getAll()}")
                takePhoto = readDao.getAll().last().bitmap
                val d:Drawable = BitmapDrawable(resources,takePhoto)
                val sample = takePhoto.toString()
                Log.d("ROOM","$takePhoto")
                Log.d("ROOM2","$sample")
            }

            profileBack.setOnClickListener {

                var firstName = editFirstName.text.toString()
                var familyName = editFamilyName.text.toString()
                val objective = editObjective.text.toString()
                val weight = editWeight.text.toString()
                if (firstName == "" && familyName == "") {
                    firstName = "あなたの名前"
                }

                val profileData = ProfileData(uri,firstName,familyName,objective,weight)
                val databaseRef = Firebase.database.reference
                databaseRef.setValue(profileData)
                findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
            }

            profileImage.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)

                intent.setType("image/*")
                startActivityForResult(intent,READ_REQUEST_CODE)
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.data.toString()
                Log.d("photo","$uri")
                try {
//                    profilePhoto = MediaStore.Images.Media.getBitmap(context?.contentResolver,uri)
                    Log.d("photo","$profilePhoto")
                }catch (e:IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}