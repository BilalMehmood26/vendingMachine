package com.buzzware.vendingmachinetech.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityMainBinding
import com.buzzware.vendingmachinetech.model.User
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : BaseActivity() {

    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setListener()

    }

    private fun setListener() {

        binding.signUpTV.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.signInTV.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }
    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            binding.progressBar.visibility = View.VISIBLE
            val userId = auth.currentUser!!.uid
            db.collection("Users").document(userId)
                .get().addOnSuccessListener { task ->
                    binding.progressBar.visibility = View.GONE
                    val user = task.toObject(User::class.java)
                    user!!.id = userId
                    UserSession.user = user
                    val intent = Intent(this@MainActivity, DashBoardActivity::class.java)
                    intent.putExtra("user","user")
                    startActivity(intent)
                    finish()
                    overridePendingTransition(
                        androidx.appcompat.R.anim.abc_fade_in,
                        androidx.appcompat.R.anim.abc_fade_out
                    )
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                }
        }
    }


}