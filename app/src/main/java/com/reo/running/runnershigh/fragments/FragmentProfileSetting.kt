package com.reo.running.runnershigh.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentProfileSettingBinding

class FragmentProfileSetting : Fragment() {

    private lateinit var binding:FragmentProfileSettingBinding
    lateinit var storage:FirebaseStorage
    private lateinit var uri: Uri
    private var firstName = ""
    private var familyName = ""
    private var objective = ""
    private var weight = ""
    private val db = Firebase.database
    private val dbPhoto = Firebase.database.getReference("profile")
    companion object {
        val READ_REQUEST_CODE = 2
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            dbPhoto.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Firebase.storage.reference.child(snapshot.value.toString())
                        .getBytes(2048 * 2048)
                        .addOnSuccessListener {
                            BitmapFactory.decodeByteArray(it, 0, it.size).also {
                                profileImage.setImageBitmap(it)
                            }
                        }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
            storage = Firebase.storage
            val databaseRefFirstName = Firebase.database.getReference("firstName")
            databaseRefFirstName.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editFirstName.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefFamily = Firebase.database.getReference("familyName")
            databaseRefFamily.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editFamilyName.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefObjective = Firebase.database.getReference("objective")
            databaseRefObjective.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editObjective.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseReferenceWeight = Firebase.database.getReference("weight")
            databaseReferenceWeight.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val weight = snapshot.value.toString()
                    editWeight.setText(weight)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            profileBack.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setIcon(R.drawable.ic_running)
                    .setTitle("記録を保存しますか？")
                    .setPositiveButton("Yes") { _, _ ->
                        firstName = editFirstName.text.toString()
                        familyName = editFamilyName.text.toString()
                        objective = editObjective.text.toString()
                        weight = editWeight.text.toString()
                        if (firstName == "" && familyName == "") {
                            familyName = "あなたの"
                            firstName = "名前"
                        }

                        if (objective == "") objective = "未登録"

                        val databaseRefFirstName = db.getReference("firstName")
                        val databaseRefFamily = db.getReference("familyName")
                        val databaseRefObjective = db.getReference("objective")
                        val databaseRefWeight = db.getReference("weight")

                        databaseRefFirstName.setValue(firstName)
                        databaseRefFamily.setValue(familyName)
                        databaseRefObjective.setValue(objective)
                        databaseRefWeight.setValue(weight)

                        findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
                    }
                    .setNegativeButton("No") { _, _ ->
                        findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
                    }
                    .show()

                profileImage.setOnClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    intent.setType("image/*")
                    startActivityForResult(intent, READ_REQUEST_CODE)
                }
            }

                // TODO 削除機能は保留
//            deleteText.setOnClickListener {
//                lifecycleScope.launch(Dispatchers.IO) {
//                    if (runDB.getAll2().isEmpty()) {
//                        withContext(Dispatchers.Main) {
//                            AlertDialog.Builder(requireContext())
//                                .setMessage("データがありません")
//                                .setPositiveButton("閉じる") { _, _, -> }
//                                .show()
//                        }
//                    } else {
//                        withContext(Dispatchers.Main) {
//                            AlertDialog.Builder(requireContext())
//                                .setMessage("データを削除しますか？")
//                                .setCancelable(false)
//                                .setPositiveButton("はい") { _, _, ->
//                                    lifecycleScope.launch {
//                                        delay(1000)
//                                        AlertDialog.Builder(requireContext())
//                                            .setMessage("もしかして、後悔しました？")
//                                            .setCancelable(false)
//                                            .setPositiveButton("はい") { _, _ ->
//                                                Toast.makeText(
//                                                    requireContext(),
//                                                    "データは残しておきましたよ！",
//                                                    Toast.LENGTH_SHORT
//                                                ).show()
//                                            }
//                                            .setNegativeButton("いいえ") { _, _ ->
//                                                lifecycleScope.launch(Dispatchers.IO) {
//                                                    Log.d("debug", "before = ${runDB.getAll2()}")
//                                                    runDB.deleteRecord2(runDB.getAll2())
//                                                    Log.d("debug", "after = ${runDB.getAll2()}")
//                                                    withContext(Dispatchers.Main) {
//                                                        Toast.makeText(
//                                                            requireContext(),
//                                                            "データを削除しました",
//                                                            Toast.LENGTH_SHORT
//                                                        ).show()
//                                                    }
//                                                }
//                                            }
//                                            .show()
//                                    }
//                                }
//                                .setNegativeButton("いいえ") { _, _ -> }
//                                .show()
//                        }
//                    }
//                }
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.data!!
                binding.profileImage.setImageURI(uri)
                val storage = Firebase.storage
                val storageRef = storage.reference
                val profileRef = storageRef.child("profiles.jpg")
                val databaseRefProfile = Firebase.database.getReference("profile")
                databaseRefProfile.setValue("profiles.jpg")
                profileRef.putFile(uri)
            }
        }
    }
}