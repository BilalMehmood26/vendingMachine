package com.buzzware.vendingmachinetech.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityLoginBinding
import com.buzzware.vendingmachinetech.databinding.LayoutAlertForgotPasswordBinding
import com.buzzware.vendingmachinetech.model.User
import com.buzzware.vendingmachinetech.utils.LocationUtility
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var locationUtility: LocationUtility

    private val RC_SIGN_IN = 100
    private var userLat = 0.0
    private var userLng = 0.0
    private var token = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

        binding.apply {
            loginTV.setOnClickListener {
                startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
                overridePendingTransition(
                    androidx.appcompat.R.anim.abc_fade_in,
                    androidx.appcompat.R.anim.abc_fade_out
                )
            }

            loginTV.setOnClickListener {
                val email = binding.emailEt.text.toString()
                val password = binding.passwordEt.text.toString()

                when {
                    email.isEmpty() -> binding.emailEt.error = "Required"
                    password.isEmpty() -> binding.passwordEt.error = "Required"
                    else -> login(email, password)
                }
            }

            forgotPasswordTv.setOnClickListener {
                showForgetDialog()
            }
        }

    }


    private fun login(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                FirebaseMessaging.getInstance().token.addOnSuccessListener {
                    updateToken(it, task.result.user!!.uid)
                }.addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                    getUserDetails(task.result.user!!.uid)
                    Log.d("LOGGER", "token: ${it.message.toString()}")
                }
            } else {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                Log.d("LOGGER", "login: ${task?.exception?.message.toString()}")
            }
        }
    }


    private fun updateToken(token: String, userID: String) {
        val user = hashMapOf(
            "token" to token,
            "isOnline" to true
        ) as Map<String, Any>
        db.collection("Users").document(userID).update(user)
            .addOnSuccessListener {
                getUserDetails(userID)
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d("LOGGER", "token FailureListener: ${it.message.toString()}")
                getUserDetails(userID)
            }
    }


    private fun getUserDetails(uid: String) {
        db.collection("Users").document(uid).get().addOnSuccessListener { response ->

            if (response.exists()) {
                binding.progressBar.visibility = View.GONE
                val user = response.toObject(User::class.java)
                UserSession.user = user!!
                val intent = Intent(this, DashBoardActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(
                    androidx.appcompat.R.anim.abc_fade_in,
                    androidx.appcompat.R.anim.abc_fade_out
                )
            }
        }.addOnFailureListener { error ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showForgetDialog() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = LayoutAlertForgotPasswordBinding.inflate(LayoutInflater.from(this))
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)

        dialogBinding.submitTv.setOnClickListener {
            val email = dialogBinding.emailET.text.toString()

            if (email.isNotEmpty()){
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Please check you email", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                Toast.makeText(this, "Please check you email", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }else{
                Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()

    }

/*

    private fun userDetails(
        fullName: String,
        email: String,
        password: String,
        token: String,
        image: String
    ) {

        locationUtility.requestLocationUpdates { currentLocation ->
            userLat = currentLocation.latitude
            userLng = currentLocation.longitude

            val memberSince = System.currentTimeMillis()
            val user = hashMapOf(
                "email" to email,
                "id" to Firebase.auth.currentUser!!.uid,
                "userName" to fullName,
                "token" to token,
                "userLat" to userLat,
                "userLng" to userLng,
                "image" to image,
                "memberSince" to memberSince,
                "password" to password
            )
            var userModel = User(
                email = email,
                id = Firebase.auth.currentUser!!.uid,
                userName = fullName,
                token = token,
                lat = userLat,
                lng = userLng,
                memberSince = memberSince,
                password = password
            )

            db.collection("Users").document(Firebase.auth.currentUser!!.uid).set(user)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    UserSession.user = userModel
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    overridePendingTransition(
                        androidx.appcompat.R.anim.abc_fade_in,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            locationUtility.removeLocationUpdates()
        }
    }
*/


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
    }
}