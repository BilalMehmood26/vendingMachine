package com.buzzware.vendingmachinetech.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.activities.VideoDetailActivity
import com.buzzware.vendingmachinetech.databinding.ItemDesignCategoryLayoutBinding
import com.buzzware.vendingmachinetech.databinding.ItemDesignVideoLayoutBinding
import com.buzzware.vendingmachinetech.model.Videos

class VideosAdapter(val context: Context, val list: ArrayList<Videos>) :
    RecyclerView.Adapter<VideosAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemDesignVideoLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDesignVideoLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.apply {
            titleTv.text = item.title
            durationTv.text = item.duration.toString()
            Glide.with(context).load(item.videoLink).into(thumbnailIv)
        }



        holder.binding.root.setOnClickListener {
            context.startActivity(Intent(context, VideoDetailActivity::class.java).putExtra("videoLink",item))
            (context as Activity).overridePendingTransition(
                androidx.appcompat.R.anim.abc_fade_in,
                androidx.appcompat.R.anim.abc_fade_out
            )
        }

    }
}