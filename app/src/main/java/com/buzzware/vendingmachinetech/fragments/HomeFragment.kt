package com.buzzware.vendingmachinetech.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.CategoryAdapter
import com.buzzware.vendingmachinetech.adapters.HomeCategoryAdapter
import com.buzzware.vendingmachinetech.adapters.VideosAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val binding : FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

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

}