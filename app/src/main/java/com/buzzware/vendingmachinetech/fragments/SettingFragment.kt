package com.buzzware.vendingmachinetech.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.activities.CategoryDetailActivity
import com.buzzware.vendingmachinetech.activities.EditActivity
import com.buzzware.vendingmachinetech.activities.LoginActivity
import com.buzzware.vendingmachinetech.activities.MainActivity
import com.buzzware.vendingmachinetech.databinding.FragmentSettingBinding
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {

    private val binding : FragmentSettingBinding by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }
    private lateinit var fragmentContext: Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        setListener()

        return binding.root
    }

    private fun setListener() {

        binding.apply {

            userNameTv.text = UserSession.user.userName
            if (!UserSession.user.image.equals("")) {
                Glide.with(fragmentContext).load(UserSession.user.image).into(profileIv)
            }
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(fragmentContext, MainActivity::class.java))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
            requireActivity().finish()
        }
        binding.accountlayout.setOnClickListener {
            startActivity(Intent(fragmentContext, EditActivity::class.java))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.favouriteLayout.setOnClickListener {
            startActivity(Intent(fragmentContext, CategoryDetailActivity::class.java).putExtra("title", "Favourites"))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.deleteLayout.setOnClickListener{
            binding.progressBar.visibility = View.VISIBLE
            val user = Firebase.auth.currentUser!!

            user.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    requireActivity().finish()
                } else {
                    binding.progressBar.visibility = View.GONE
                    Log.d("Logger", "setListener: ${task.exception!!.message}")
                    Toast.makeText(
                        fragmentContext,
                        "${task.exception!!.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }



    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}