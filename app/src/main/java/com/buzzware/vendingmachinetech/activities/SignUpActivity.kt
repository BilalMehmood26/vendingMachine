package com.buzzware.vendingmachinetech.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivitySignUpBinding
import com.buzzware.vendingmachinetech.model.User
import com.buzzware.vendingmachinetech.utils.LocationUtility
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SignUpActivity : AppCompatActivity() {

    private val binding : ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var locationUtility: LocationUtility
    val REQUEST_CODE = 1000
    private var userLat = 0.0
    private var userLng = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (ActivityCompat.checkSelfPermission(
                this@SignUpActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@SignUpActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@SignUpActivity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
        }
        locationUtility = LocationUtility(this@SignUpActivity)


        setListener()

    }

    private fun setListener() {

        binding.apply {
            loginTV.setOnClickListener {
                finish()
            }

            getStartBtn.setOnClickListener {
                val email = emailEt.text.toString()
                val userName = fullNameEt.text.toString()
                val password = passwordEt.text.toString()
                val phoneNumber = phoneNumberEt.text.toString()

                when {
                    userName.isEmpty() -> fullNameEt.error = "Required"
                    email.isEmpty() -> emailEt.error = "Required"
                    password.isEmpty() -> passwordEt.error = "Required"
                    phoneNumber.isEmpty() -> phoneNumberEt.error = "Required"
                    else -> signUp(userName, email, password,phoneNumber)
                }
                //startActivity(Intent(this@SignUpActivity, DashboardActivity::class.java))
            }
        }
        binding.backIV.setOnClickListener { onBackPressed() }
/*
        binding.getStartBtn.setOnClickListener {
            //startActivity(Intent(this, SubscriptionActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }*/

    }

    private fun signUp(fullName: String, email: String, password: String,phoneNumber:String) {
        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            FirebaseMessaging.getInstance().token.addOnSuccessListener {
                Log.d("LOGGER", "signUp: Token$it")
                userDetails(fullName, email, password, it,phoneNumber)
            }.addOnFailureListener {
                Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                userDetails(fullName, email, password, "",phoneNumber)
            }
        }.addOnFailureListener {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this@SignUpActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun userDetails(fullName: String, email: String, password: String, token: String,phoneNumber:String) {
        Log.d("LOGGER", "signUp: User Details")
        locationUtility.requestLocationUpdates { currentLocation ->
            userLat = currentLocation.latitude
            userLng = currentLocation.longitude

            Log.d("LOGGER", "signUp: currentLocation ${currentLocation.latitude}")
            val createdAt = System.currentTimeMillis()
            val user = hashMapOf(
                "email" to email,
                "id" to Firebase.auth.currentUser!!.uid,
                "userName" to fullName,
                "createdAt" to createdAt,
                "isActive" to true,
                "token" to token,
                "isPro" to false,
                "subscriptionType" to "trail",
                "userType" to "user",
                "userLat" to userLat,
                "userLng" to userLng,
                "image" to "",
                "password" to password,
                "phoneNumber" to phoneNumber
            )
            var userModel = User(
                id = Firebase.auth.currentUser!!.uid,
                email = email,
                userName = fullName,
                phoneNumber = phoneNumber,
                password = password,
                token = token,
                userLat = userLat,
                userLng = userLng,
                isActive = true,
                isPro = false,
                subscriptionType = "trail",
                createdAt = createdAt,
                userType = "user"
            )

            db.collection("Users").document(Firebase.auth.currentUser!!.uid).set(user)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    UserSession.user = userModel
                    val intent = Intent(this, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(
                        androidx.appcompat.R.anim.abc_fade_in,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@SignUpActivity, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            locationUtility.removeLocationUpdates()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}