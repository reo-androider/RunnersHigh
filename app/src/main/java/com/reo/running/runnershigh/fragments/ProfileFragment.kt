package com.reo.running.runnershigh.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private var login = ""
    private val databaseReferenceLogin = Firebase.database.getReference("Login")
    private val databaseReferenceLoginDay = Firebase.database.getReference("LoginDay")
    private val databaseReferencePhoto = Firebase.database.getReference("profile")
    private lateinit var auth:FirebaseAuth
    private val runDB = MyApplication.db.recordDao2()
    private var firstId:Int = 0
    private var lastId:Int = 0
    private var i:Int? = 0  //カウント変数用
    private var totalDistance = 0.0
    private var totalCalorie = 0
    private var alienCount = 0
    private var fatCount = 0
    private val db = Firebase.database
    companion object {
        private const val RC_SIGN_IN = 123
    }

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
                        Firebase.storage.reference.child(snapshot.value.toString()).getBytes(2048 * 2048)
                            .addOnSuccessListener {
                                BitmapFactory.decodeByteArray(it,0,it.size).also {
                                    profileImage.setImageBitmap(it)
                                    profileImageDefault.visibility = View.GONE
                                }
                            }.addOnFailureListener { Log.d("debug","failure ${it.cause}") }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            auth = FirebaseAuth.getInstance()
            val user = Firebase.auth.currentUser
            if (user != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val data = runDB.getAll2()
                    if (data.isNotEmpty()) {
                        firstId = data.first().id
                        lastId = data.last().id
                        if (lastId != null) {
                            for (i in firstId..lastId - 1) {
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
                            totalDistance.let {
                                when {
                                    it < 5 -> {
                                        distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_level0))
                                        distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_0)}"
                                    }
                                    it < 10 -> {
                                        distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_5km))
                                        distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_5)}"
                                    }
                                    it > 80 && it > 100 -> {
                                        distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_mountain))
                                        distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_10)}"
                                    }
                                    it < 100 -> {
                                        distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_plane))
                                        distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_50)}"
                                    }
                                    it > 100 -> {
                                        distanceLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_space_human))
                                        distanceLevelText.text = "${resources.getString(R.string.profile_distance_num_metaphor_100)}"
                                    }
                                }
                            }

                            totalCalorie.let {
                                when {
                                    it / 100 < 1 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_level0))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_1)}"
                                    }
                                    it / 100 > 1 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_rice_ball))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_2)}"
                                    }
                                    it / 100 > 3 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_hamberger))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_3)}"
                                    }
                                    it / 100 > 4 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_cake))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_4)}"
                                    }
                                    it / 100 > 10 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_curry))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_10)}"
                                    }
                                    it / 100 > 2 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_ramen))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_20)}"
                                    }
                                    it / 100 > 2 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_pizza))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_30)}"
                                    }
                                    it / 100 > 2 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_rice300))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_48)}"
                                    }
                                    it / 100 > 72 -> {
                                        calorieLevelImage.setImageDrawable(resources.getDrawable(R.drawable.ic_meat))
                                        calorieLevelText.text = "${resources.getString(R.string.profile_calorie_metaphor_72)}"
                                    }
                                }
                            }
                        }
                    }
                }
                Toast.makeText(requireContext(),"Loginされています",Toast.LENGTH_SHORT).show()
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

            val databaseRefFirstName = db.getReference("firstName")
            databaseRefFirstName.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value


                    profileFirstName.text = "$fireStore"
                    profileFirstName.setTextColor(resources.getColor(R.color.normal2))
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            val databaseRefFamily = db.getReference("familyName")
            databaseRefFamily.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fireStore = snapshot.value
                    profileFamilyName.text = "$fireStore"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
            val databaseRefObjective = db.getReference("objective")
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

//            必要なので残しておく
//            logoutImage.setOnClickListener {
//                AuthUI.getInstance()
//                    .delete(requireContext())
//            }

            explainDistanceLevel.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("距離レベルとは")
                    .setMessage("\n今まで走った距離に応じてレベルアップ\n※1kmごとにレベルが1上がる\n\n " +
                            "また、その距離がどれくらい凄いかを\n比喩を用いてお教えします^_^" +
                            "\n\nログインするとお楽しみ頂けます")
                    .show()
            }

            explainCalorieLevel.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("消費カロリーレベルとは")
                    .setMessage("\n総消費カロリーに応じてレベルアップ\n※100kcalごとにレベルが1上がる" +
                            "\n\nまた、その総消費カロリーがどれくらい\n凄いかを比喩を用いてお教えします^_^" +
                            "\n\nログインするとお楽しみ頂けます")
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
//            AuthUI.IdpConfig.EmailBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build()
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
                db.setPersistenceEnabled(true)
                binding.run {
                    loginText.visibility = View.VISIBLE
                    val day = loginDate().toString()
                    databaseReferenceLoginDay.setValue(day)
                    loginDay.text = day
                    loginImage.visibility = View.GONE
//                binding.logoutImage.visibility = View.VISIBLE 必要なので残しておく
                    login = "true"
                    databaseReferenceLogin.setValue(login)
                    val user = FirebaseAuth.getInstance().currentUser
                }

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

    /*
    resisterForA


     */
}