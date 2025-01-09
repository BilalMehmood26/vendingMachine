package com.buzzware.vendingmachinetech.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.NotificationAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentNotificationBinding

class NotificationFragment : Fragment() {

    private val binding : FragmentNotificationBinding by lazy {
        FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        binding.notificationRV.layoutManager = LinearLayoutManager(requireActivity())
        binding.notificationRV.adapter = NotificationAdapter(requireActivity(), arrayListOf())
    }

}