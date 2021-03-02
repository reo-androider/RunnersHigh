package com.reo.running.runnershigh.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import java.util.*

class ProfileSettingFragment : Fragment() {

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
        const val READ_REQUEST_CODE = 2
    }
    var deletePath = "" //写真更新の際削除する為
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileSettingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            dbPhoto.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    deletePath = snapshot.value.toString()
                    Firebase.storage.reference.child(deletePath)
                        .getBytes(2048 * 2048)
                        .addOnSuccessListener {
                            BitmapFactory.decodeByteArray(it, 0, it.size).also {
                                profileImageDefault.setImageBitmap(it)
                            }
                        }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            storage = Firebase.storage
            val databaseRefFirstNameOut = Firebase.database.getReference("firstName")
            databaseRefFirstNameOut.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editFirstName.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefFamilyOut = Firebase.database.getReference("familyName")
            databaseRefFamilyOut.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editFamilyName.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefObjectiveOut = Firebase.database.getReference("objective")
            databaseRefObjectiveOut.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value.toString()
                    editObjective.setText(fireStore)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefWeightOut = Firebase.database.getReference("weight")
            databaseRefWeightOut.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val weight = snapshot.value.toString()
                    editWeight.setText(weight)
                }

                override fun onCancelled(error: DatabaseError) {}
            })

            profileBack.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setCancelable(true)
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

                        val databaseRefFirstNameIn = db.getReference("firstName")
                        val databaseRefFamilyIn = db.getReference("familyName")
                        val databaseRefObjectiveIn = db.getReference("objective")
                        val databaseRefWeightIn = db.getReference("weight")

                        databaseRefFirstNameIn.setValue(firstName)
                        databaseRefFamilyIn.setValue(familyName)
                        databaseRefObjectiveIn.setValue(objective)
                        databaseRefWeightIn.setValue(weight)

                        findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
                    }
                    .setNegativeButton("No") { _, _ ->
                        findNavController().navigate(R.id.action_fragmentProfileSetting_to_navi_setting2)
                    }
                    .show()
            }

            profileImageDefault.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.setType("image/*")
                startActivityForResult(intent, READ_REQUEST_CODE)
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
                uri = data.data as Uri
                binding.profileImageDefault.setImageURI(uri)
                val storage = Firebase.storage
                val storageRef = storage.reference
                val uid = makeUid()
                val profileRef = storageRef.child(":$uid/profiles.jpg")
                val databaseRefProfile = Firebase.database.getReference("profile")
                databaseRefProfile.setValue(":$uid/profiles.jpg")
                profileRef.putFile(uri)
                storageRef.child(deletePath).delete()
                        .addOnSuccessListener {
                            Log.d("debug","Success=$it")
                        }
                        .addOnFailureListener {
                            Log.d("debug","Failure=$it")
                        }
            }
        }
    }

    fun makeUid(): String {
        return UUID.randomUUID().toString()
    }
}