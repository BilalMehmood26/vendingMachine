package com.buzzware.vendingmachinetech.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.CategoryAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    private val binding : FragmentCategoryBinding by lazy {
        FragmentCategoryBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        binding.categoryRV.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.categoryRV.adapter = CategoryAdapter(requireActivity(), arrayListOf())
    }

}