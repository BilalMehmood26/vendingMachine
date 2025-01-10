package com.buzzware.vendingmachinetech.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityEditBinding
import com.buzzware.vendingmachinetech.utils.UserSession
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class EditActivity : BaseActivity() {

    private val binding: ActivityEditBinding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    lateinit var imageURI: Uri
    private val mAuth = Firebase.auth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

        binding.apply {

            firstNameTv.setText(UserSession.user.userName)
            emailTv.setText(UserSession.user.email)
            passwordTv.setText(UserSession.user.password)
            if (!UserSession.user.image.equals("")) {
                Glide.with(this@EditActivity).load(UserSession.user.image).into(profileIv)
            }

            profileIv.setOnClickListener {
                openGalleryOrFilePicker()
            }

            backIV.setOnClickListener {
                finish()
            }

            saveTV.setOnClickListener {
                saveInfo(
                    firstNameTv.text.toString(),
                    passwordTv.text.toString(),
                    emailTv.text.toString()
                )
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Glide.with(this).load(data?.data).into(binding.profileIv)
            imageURI = data!!.data!!
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveInfo(
        userName: String,
        password: String,
        email: String
    ) {

        binding.progressBar.visibility = View.VISIBLE
        if (imageURI != null) {
            val storage =
                FirebaseStorage.getInstance().reference.child("Users/${mAuth.currentUser!!.uid}.jpg")

            var uploadTask = storage.putFile(imageURI!!)
            uploadTask.addOnSuccessListener {
                storage.downloadUrl.addOnSuccessListener {
                    val userUpdate = hashMapOf(
                        "userName" to userName,
                        "password" to password,
                        "email" to email,
                        "image" to it.toString()
                    ) as Map<String, Any>
                    UserSession.user.image = it.toString()

                    db.collection("Users").document(mAuth.currentUser!!.uid).update(userUpdate)
                        .addOnSuccessListener {
                            UserSession.user.userName = userName
                            UserSession.user.email = email
                            UserSession.user.password = password
                            Toast.makeText(this@EditActivity, "Profile Updated", Toast.LENGTH_SHORT)
                                .show()
                            if (password.isNotEmpty()) {
                                updatePassword(password)
                            }
                        }.addOnFailureListener { error ->
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@EditActivity,
                                error.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditActivity, it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        } else {
            val userUpdate = hashMapOf(
                "firstName" to userName,
                "password" to password,
                "email" to email,
            ) as Map<String, Any>
            db.collection("Users").document(mAuth.currentUser!!.uid).update(userUpdate)
                .addOnSuccessListener {
                    UserSession.user.userName = userName
                    UserSession.user.email = email
                    UserSession.user.password = password
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@EditActivity, "Profile Updated", Toast.LENGTH_SHORT)
                        .show()
                    if (password.isNotEmpty()) {
                        updatePassword(password)
                    }
                }.addOnFailureListener {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@EditActivity,
                        it.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }

    private fun updatePassword(password: String) {
        binding.progressBar.visibility = View.VISIBLE
        val credential =
            EmailAuthProvider.getCredential(UserSession.user.email!!, UserSession.user.password!!)
        Firebase.auth.currentUser?.reauthenticate(credential)
            ?.addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    Firebase.auth.currentUser!!.updatePassword(password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                binding.progressBar.visibility = View.GONE
                                Log.d("LOGGER!", "Password updated successfully.")
                            } else {
                                binding.progressBar.visibility = View.GONE
                                Log.e(
                                    "LOGGER!",
                                    "Error updating password: ${task.exception?.message}"
                                )
                                Toast.makeText(
                                    this@EditActivity,
                                    "${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@EditActivity,
                        "${reauthTask.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("LOGGER!", "Re-authentication failed: ${reauthTask.exception?.message}")
                }
            }
    }

    private fun openGalleryOrFilePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
    }
}