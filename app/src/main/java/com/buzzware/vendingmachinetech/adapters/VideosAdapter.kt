package com.buzzware.vendingmachinetech.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.activities.VideoDetailActivity
import com.buzzware.vendingmachinetech.databinding.ItemDesignCategoryLayoutBinding
import com.buzzware.vendingmachinetech.databinding.ItemDesignVideoLayoutBinding
import com.buzzware.vendingmachinetech.model.Videos
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class VideosAdapter(val context: Context, val list: ArrayList<Videos>) :
    RecyclerView.Adapter<VideosAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

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
            durationTv.text = convertMillisecondsToTimeFormat(item.duration)
            Glide.with(context).load(item.videoLink).into(thumbnailIv)

            if (item.isFavorite) {
                favouriteIv.setImageResource(R.drawable.ic_heart_post_fill)
            } else {
                favouriteIv.setImageResource(R.drawable.ic_heart_post)
            }

            favouriteIv.setOnClickListener {
                if (item.isFavorite) {
                    db.collection("Videos").document(item.postId).update("isFavorite", false)
                    db.collection("Users").document(Firebase.auth.currentUser!!.uid).update("favorites", FieldValue.arrayRemove(item.postId))
                    favouriteIv.setImageResource(R.drawable.ic_heart_post)
                } else {
                    db.collection("Videos").document(item.postId).update("isFavorite", true)
                    db.collection("Users").document(Firebase.auth.currentUser!!.uid).update("favorites", FieldValue.arrayUnion(item.postId))
                    favouriteIv.setImageResource(R.drawable.ic_heart_post_fill)
                }
            }
        }



        holder.binding.root.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    VideoDetailActivity::class.java
                ).putExtra("videoLink", item)
            )
            (context as Activity).overridePendingTransition(
                androidx.appcompat.R.anim.abc_fade_in,
                androidx.appcompat.R.anim.abc_fade_out
            )
        }

    }

    private fun convertMillisecondsToTimeFormat(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        return when {
            totalSeconds >= 3600 -> {
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                String.format("%02d:%02d", hours, minutes)
            }

            totalSeconds <= 59 -> {
                if (totalSeconds <= 9) {
                    String.format("00:0%d", totalSeconds)
                } else {
                    String.format("00:%02d", totalSeconds)
                }
            }

            else -> {
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                String.format("%02d:%02d", minutes, seconds)
            }
        }
    }
}