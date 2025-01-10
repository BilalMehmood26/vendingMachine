package com.buzzware.vendingmachinetech.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.activities.CategoryDetailActivity
import com.buzzware.vendingmachinetech.activities.EditActivity
import com.buzzware.vendingmachinetech.databinding.FragmentSettingBinding
import com.buzzware.vendingmachinetech.utils.UserSession

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
        binding.accountlayout.setOnClickListener {
            startActivity(Intent(requireActivity(), EditActivity::class.java))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.favouriteLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryDetailActivity::class.java).putExtra("title", "Favourites"))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}