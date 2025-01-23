package com.buzzware.vendingmachinetech.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.activities.CategoryDetailActivity
import com.buzzware.vendingmachinetech.activities.VideoDetailActivity
import com.buzzware.vendingmachinetech.databinding.ItemDesignCategoryLayoutBinding
import com.buzzware.vendingmachinetech.model.Category

class CategoryAdapter(val context: Context, private val list: ArrayList<Category>)
    :RecyclerView.Adapter<CategoryAdapter.ViewHolder>(){


    inner class ViewHolder(val binding : ItemDesignCategoryLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignCategoryLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.apply {
            titleTv.text = item.title
            if(item.Image!!.isNotEmpty()){
                Glide.with(context).load(item.Image).into(categoryIv)
            }
        }
        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context, CategoryDetailActivity::class.java).apply{
                putExtra("title", item.title)
                putExtra("categoryID", item.id)
            })
            (context as Activity).overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }

    }
}