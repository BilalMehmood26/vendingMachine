package com.buzzware.vendingmachinetech.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.activities.CategoryDetailActivity
import com.buzzware.vendingmachinetech.activities.EditActivity
import com.buzzware.vendingmachinetech.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private val binding : FragmentSettingBinding by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        setListener()

        return binding.root
    }

    private fun setListener() {

        binding.accountlayout.setOnClickListener {
            startActivity(Intent(requireActivity(), EditActivity::class.java))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

        binding.favouriteLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), CategoryDetailActivity::class.java).putExtra("title", "Favourites"))
            requireActivity().overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }

}