package com.buzzware.vendingmachinetech.fragments

import android.content.Context
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

    private lateinit var fragmentContext: Context
    private val binding : FragmentNotificationBinding by lazy {
        FragmentNotificationBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        binding.notificationRV.layoutManager = LinearLayoutManager(fragmentContext)
        binding.notificationRV.adapter = NotificationAdapter(fragmentContext, arrayListOf())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}