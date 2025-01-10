package com.buzzware.vendingmachinetech.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.CategoryAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentCategoryBinding
import com.buzzware.vendingmachinetech.model.Category
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryFragment : Fragment() {

    private val binding : FragmentCategoryBinding by lazy {
        FragmentCategoryBinding.inflate(layoutInflater)
    }

    private lateinit var fragmentContext: Context
    private val categoryList :ArrayList<Category> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        getCategoryList()

        return binding.root
    }

    private fun getCategoryList (){
        Firebase.firestore.collection("Categories").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(fragmentContext, "", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.VISIBLE
                return@addSnapshotListener
            }

            binding.progressBar.visibility = View.GONE
            categoryList.clear()
            value!!.forEach {
                val video = it.toObject(Category::class.java)
                categoryList.add(video)
            }
            setAdapter()
        }
    }

    private fun setAdapter() {
        binding.categoryRV.layoutManager = GridLayoutManager(requireActivity(), 3)
        binding.categoryRV.adapter = CategoryAdapter(requireActivity(), categoryList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}