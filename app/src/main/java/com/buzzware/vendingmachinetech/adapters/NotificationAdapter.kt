package com.buzzware.vendingmachinetech.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.buzzware.vendingmachinetech.databinding.ItemDesignNotificationLayoutBinding

class NotificationAdapter(val context: Context, list: ArrayList<String>)
    :RecyclerView.Adapter<NotificationAdapter.ViewHolder>(){

    inner class ViewHolder(val binding : ItemDesignNotificationLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDesignNotificationLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.root.setOnClickListener {

        }

    }
}