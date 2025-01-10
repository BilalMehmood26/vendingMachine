package com.buzzware.vendingmachinetech.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.CategoryAdapter
import com.buzzware.vendingmachinetech.adapters.HomeCategoryAdapter
import com.buzzware.vendingmachinetech.adapters.VideosAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentHomeBinding
import com.buzzware.vendingmachinetech.utils.UserSession
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private val binding : FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding.apply {
            dateTv.text = formatTimestampToDateString(System.currentTimeMillis())
            userNameTv.text = "Hello ${UserSession.user.userName}!"
        }
        setCategory()
        setAdapter()

        return binding.root
    }

    private fun setCategory() {
        binding.categoryRV.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRV.adapter = HomeCategoryAdapter(requireActivity(), arrayListOf())
    }

    private fun setAdapter() {
        binding.continueRV.layoutManager = LinearLayoutManager(requireActivity())
        binding.continueRV.adapter = VideosAdapter(requireActivity(), arrayListOf())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTimestampToDateString(timestamp: Long): String {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
        return localDateTime.format(formatter)
    }
}