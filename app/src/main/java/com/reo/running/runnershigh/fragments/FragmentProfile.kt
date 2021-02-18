package com.reo.running.runnershigh.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.reo.running.runnershigh.MyApplication
import com.reo.running.runnershigh.R
import com.reo.running.runnershigh.databinding.FragmentProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
    private val databaseReferencePhoto = Firebase.database.getReference("profile")
    private lateinit var auth:FirebaseAuth
//    private lateinit var googleSignInClient:GoogleSignInClient
//    private val isSignIn:Boolean
//        get() = auth.currentUser != null
    private val runDB = MyApplication.db.recordDao2()
    private var lastId:Int = 0
    private var i:Int? = 0  //カウント変数用
    private var totalDistance = 0.0
    private var totalCalorie = 0
    private var alienCount = 0
    private var fatCount = 0

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
                databaseReferencePhoto.addValueEventListener(object:ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("photo20","${snapshot.value}")
                        Firebase.storage.reference.child(snapshot.value.toString()).downloadUrl
                            .addOnFailureListener {
                                Log.d("exception2020","${it.cause}")
                            }
                            .addOnSuccessListener {
                                Log.d("uri","$it")
                                val someFile = File(it.toString())
                                profileImage.setImageURI(Uri.parse(it.toString()))
                            }

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
//                val gsReference = this
//                firebasePhoto.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        Log.d("photo5","${snapshot.value}")
////
//                    }
//                    override fun onCancelled(error: DatabaseError) {}
//                })
            auth = FirebaseAuth.getInstance()
            val user = Firebase.auth.currentUser
            if (user != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = runDB.getAll2()
                    lastId = data.last().id - 1
                    if (lastId != null) {
                        for (i in 0..lastId) {
                            totalDistance += data[i].distance
                            totalCalorie += data[i].calorie
                        }
                    }
                    withContext(Dispatchers.Main) {
                        distanceLevel.text = "Lv.${totalDistance.toInt() / 1000}"
                        alienCount = totalDistance.toInt() / 100000
                        spaceManCount.text = "$alienCount"
                        calorieLevel.text = "Lv.${totalCalorie / 100}"
                        fatCount = totalCalorie / 7200
                        calorie1kgCount.text = "$fatCount"
                        when{
                            totalDistance < 5 -> {
                                distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_level0))
                                distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_0)}"
                            }
                            totalDistance < 10 -> {
                                distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_5km))
                                distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_5)}"
                            }
                            totalDistance > 80 && totalDistance > 100  -> {
                                distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_mountain))
                                distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_10)}"
                            }
                            totalDistance < 100 -> {
                                distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_plane))
                                distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_50)}"
                            }
                            totalDistance > 100 -> {
                                distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_space_human))
                                distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_100)}"
                            }
                        }

                        when {
                            totalCalorie /100 < 1 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_level0))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_1)}"
                            }
                            totalCalorie / 100 > 1 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_rice_ball))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_2)}"
                            }
                            totalCalorie / 100 > 3 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_hamberger))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_3)}"
                            }
                            totalCalorie / 100 > 4 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_cake))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_4)}"
                            }
                            totalCalorie / 100 > 10 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_curry))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_10)}"
                            }
                            totalCalorie / 100 > 2 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_ramen))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_20)}"
                            }
                            totalCalorie / 100 > 2 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_pizza))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_30)}"
                            }
                            totalCalorie / 100 > 2 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_rice300))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_48)}"
                            }
                            totalCalorie / 100> 72 -> {
                                calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_meat))
                                calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_72)}"
                            }
                        }
                    }
                }
                Toast.makeText(requireContext(),"Loginされています",Toast.LENGTH_SHORT).show()
//                databaseReferenceLogin.setValue(login)
                databaseReferenceLogin.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        loginImage.visibility = View.GONE
//                      必要なので残しておく（ログアウト処理を実装するために）
//                        logoutImage.visibility = View.VISIBLE
                        loginText.visibility = View.VISIBLE
                        spaceMan.visibility = View.VISIBLE
                        spaceManCount.visibility = View.VISIBLE
                        calorie1kgImage.visibility = View.VISIBLE
                        calorie1kgCount.visibility = view.visibility
                        databaseReferenceLoginDay.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val day = snapshot.value.toString()
                                loginDay.text = day
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    override fun onCancelled(error: DatabaseError) {} })

                calorieLock.visibility = View.GONE
                distanceLock.visibility = View.GONE
                distanceLevel.visibility = View.VISIBLE
                distanceLevelText.visibility = View.VISIBLE
                distanceLevelImage.visibility = View.VISIBLE
                calorieLevel.visibility = View.VISIBLE
                calorieLevelText.visibility = View.VISIBLE
                calorieLevelImage.visibility = View.VISIBLE

            } else {
                Toast.makeText(requireContext(),"Loginされていません",Toast.LENGTH_LONG).show()
            }
//
//            val databaseRefPhoto = Firebase.database.getReference("photo")
//            databaseRefPhoto.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.value != null) {
//                        val fireStore = snapshot.value
//                        // TODO
////                            profileImage.setImageURI(Uri.parse(fireStore.toString()))
//                    }
//                }
//                override fun onCancelled(error: DatabaseError) {}
//            })

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

            explainDistanceLevel.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("距離レベルとは")
                    .setMessage("\n" + "今まで走った距離に応じてレベルアップ" + "\n" + "※1kmごとにレベルが1上がる" +  "\n\n" +
                            "また、その距離がどれくらい凄いかを" + "\n" + "比喩を用いてお教えします^_^" + "\n\n" +
                    "ログインするとお楽しみ頂けます")
                    .show()
            }

            explainCalorieLevel.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("消費カロリーレベルとは")
                    .setMessage("\n" + "総消費カロリーに応じてレベルアップ" + "\n" + "※100kcalごとにレベルが1上がる" + "\n\n"
                            + "また、その総消費カロリーがどれくらい" + "\n" + "凄いかを比喩を用いてお教えします^_^"
                    + "\n\n" + "ログインするとお楽しみ頂けます")
                    .show()
            }

            spaceMan.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("宇宙人にあった回数  ${alienCount}回")
                    .setMessage("※100km走るごとに会えるよ\n※?回目に会うと…")
                    .show()
            }

            calorie1kgImage.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("1kgの脂肪を消費した回数   ${fatCount}回")
                    .setMessage("※7200kcal消費ごとにカウント\n※?回目に達すると…")
                    .show()
            }

            folderImage.setOnClickListener {
                findNavController().navigate(R.id.action_navi_setting_to_fragmentPhoto)
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
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
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