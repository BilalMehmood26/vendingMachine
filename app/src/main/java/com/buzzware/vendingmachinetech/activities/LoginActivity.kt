package com.buzzware.vendingmachinetech.activities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityLoginBinding
import com.buzzware.vendingmachinetech.databinding.LayoutAlertForgotPasswordBinding
import com.buzzware.vendingmachinetech.model.CustomerResponse
import com.buzzware.vendingmachinetech.model.Subscription
import com.buzzware.vendingmachinetech.model.User
import com.buzzware.vendingmachinetech.stripe.Controller
import com.buzzware.vendingmachinetech.utils.LocationUtility
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private lateinit var locationUtility: LocationUtility
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 100
    private var userLat = 0.0
    private var userLng = 0.0
    private val REQUEST_CODE = 1000
    private var token = ""
    private var endDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("904974557339-odhbtb7hm3sj46l0mbgp0jv3684o99m1.apps.googleusercontent.com")
            .requestEmail()
            .build()

        if (ActivityCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@LoginActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@LoginActivity,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_CODE
            )
        }
        locationUtility = LocationUtility(this@LoginActivity)
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

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

            googleBtn.setOnClickListener {
                mGoogleSignInClient.signOut().addOnCompleteListener(this@LoginActivity) {
                    val signInIntent = mGoogleSignInClient.signInIntent
                    startActivityForResult(signInIntent, RC_SIGN_IN)
                }
            }

            forgotPasswordTv.setOnClickListener {
                showForgetDialog()
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            binding.progressBar.visibility = View.VISIBLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.let {
                    val idToken = account.idToken

                    FirebaseMessaging.getInstance().token.addOnCompleteListener {
                        token = it.result
                        Log.d("GoogleSignIn", "token : $token");
                    }
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener { authResult ->
                            val userId = auth.currentUser?.uid.toString()
                            Log.d("GoogleSignIn", "Google Auth Success: $userId")
                            db.collection("Users").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        Log.d("GoogleSignIn", "Google Auth User Exists")
                                        val user = document.toObject(User::class.java)
                                        user!!.id = userId
                                        UserSession.user = user
                                        UserSession.user.isPro = document.getBoolean("isPro")!!
                                        if (user.isPro) {
                                            if (subscriptionExpire().not()) {
                                                val intent =
                                                    Intent(this, DashBoardActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                                overridePendingTransition(
                                                    androidx.appcompat.R.anim.abc_fade_in,
                                                    androidx.appcompat.R.anim.abc_fade_out
                                                )
                                            } else {
                                                val intent =
                                                    Intent(this, SubscriptionActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                                overridePendingTransition(
                                                    androidx.appcompat.R.anim.abc_fade_in,
                                                    androidx.appcompat.R.anim.abc_fade_out
                                                )
                                            }
                                        } else {
                                            val intent = Intent(
                                                this@LoginActivity,
                                                SubscriptionActivity::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        }

                                    } else {
                                        Log.d("GoogleSignIn", "Google Auth New User")
                                        createCustomer(
                                            account.displayName.toString(),
                                            account.email.toString(),
                                            token,
                                            account.idToken!!,
                                            account.photoUrl.toString(),
                                            userId
                                        )
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.d(
                                        "GoogleSignIn",
                                        "Error fetching user: ${exception.message}"
                                    )
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Failed to fetch user data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.d("GoogleSignIn", "Google Auth Failed: ${exception.message}")
                            Toast.makeText(
                                this@LoginActivity,
                                "Authentication Failed: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            } catch (e: ApiException) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
                Log.e("GoogleSignIn", "Google sign-in failed with ApiException: ${e.statusCode} ${e.message }")
                e.printStackTrace()
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
                    getSubscription(task.result.user!!.uid)
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
                getSubscription(userID)
            }.addOnFailureListener {
                Toast.makeText(this@LoginActivity, it.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d("LOGGER", "token FailureListener: ${it.message.toString()}")
                getSubscription(userID)
            }
    }


    private fun getUserDetails(uid: String) {
        db.collection("Users").document(uid).get().addOnSuccessListener { response ->

            if (response.exists()) {
                binding.progressBar.visibility = View.GONE
                val user = response.toObject(User::class.java)
                UserSession.user = user!!
                if (subscriptionExpire().not()) {
                    val intent = Intent(this, DashBoardActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(
                        androidx.appcompat.R.anim.abc_fade_in,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                } else {
                    val intent = Intent(this, SubscriptionActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(
                        androidx.appcompat.R.anim.abc_fade_in,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                }
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
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)

        dialogBinding.submitTv.setOnClickListener {
            val email = dialogBinding.emailET.text.toString()

            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Please check you email", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
                Toast.makeText(this, "Please check you email", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Email Required", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()

    }

    private fun createCustomer(
        fullName: String,
        email: String,
        password: String,
        token: String,
        image: String,
        id: String
    ) {
        binding.progressBar.visibility = View.VISIBLE
        val body = mapOf("email" to email, "userId" to id)
        Controller.instance.createCustomer(body).enqueue(object : Callback<CustomerResponse> {
            override fun onResponse(
                call: Call<CustomerResponse>,
                response: Response<CustomerResponse>
            ) {
                if (response.isSuccessful) {
                    userDetails(
                        fullName,
                        email,
                        password,
                        token,
                        image,
                        response.body()!!.cust_id,
                        response.body()!!.account_id
                    )
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Logger", "onResponse: ${response.errorBody()!!.string()}")
                    Toast.makeText(
                        this@LoginActivity,
                        response.errorBody()!!.string(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<CustomerResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT).show()

            }
        })
    }

    private fun userDetails(
        fullName: String,
        email: String,
        password: String,
        token: String,
        image: String,
        custID: String,
        accountID: String
    ) {

        locationUtility.requestLocationUpdates { currentLocation ->
            userLat = currentLocation.latitude
            userLng = currentLocation.longitude

            val createdAt = System.currentTimeMillis()
            val user = hashMapOf(
                "email" to email,
                "id" to Firebase.auth.currentUser!!.uid,
                "userName" to fullName,
                "createdAt" to createdAt,
                "isActive" to true,
                "token" to token,
                "isPro" to false,
                "userType" to "user",
                "userLat" to userLat,
                "userLng" to userLng,
                "image" to image,
                "password" to password,
                "phoneNumber" to "",
                "stripeCustid" to custID,
                "stripeaccount_id" to accountID
            )

            var userModel = User(
                id = Firebase.auth.currentUser!!.uid,
                email = email,
                userName = fullName,
                phoneNumber = "",
                password = password,
                token = token,
                userLat = userLat,
                userLng = userLng,
                isActive = true,
                isPro = false,
                image = image,
                subscriptionType = "trail",
                createdAt = createdAt,
                userType = "user",
                stripeCustid = custID,
                stripeaccount_id = accountID
            )

            db.collection("Users").document(Firebase.auth.currentUser!!.uid).set(user)
                .addOnSuccessListener {
                    binding.progressBar.visibility = View.GONE
                    UserSession.user = userModel
                    val intent = Intent(this, SubscriptionActivity::class.java)
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

    private fun getSubscription(uid: String) {
        binding.progressBar.visibility = View.VISIBLE
        db.collection("Subscriptions").document(Firebase.auth.currentUser!!.uid).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    val subscription = it.result.toObject(Subscription::class.java)
                    endDate = subscription!!.endDate
                    getUserDetails(uid)
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "${it.exception!!.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun subscriptionExpire(): Boolean {
        val currentDate = System.currentTimeMillis()
        if (currentDate <= endDate) {
            return false
        } else {
            return true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
    }
}