package com.buzzware.vendingmachinetech.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.activities.VideoDetailActivity
import com.buzzware.vendingmachinetech.databinding.ItemDesignCategoryLayoutBinding
import com.buzzware.vendingmachinetech.databinding.ItemDesignVideoLayoutBinding
import com.buzzware.vendingmachinetech.model.Videos
import com.buzzware.vendingmachinetech.utils.UserSession
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
            Log.d("LOGGER", "onBindViewHolder: ${item.duration}")
            Glide.with(context).load(item.videoLink).into(thumbnailIv)

            item.favorites?.forEach {
                if (it.equals(Firebase.auth.currentUser!!.uid)) {
                    favouriteIv.setImageResource(R.drawable.ic_heart_post_fill)
                } else {
                    favouriteIv.setImageResource(R.drawable.ic_heart_post)
                }
            }

            favouriteIv.setOnClickListener {
                if(item.favorites!!.isNotEmpty()){
                    item.favorites?.forEach {
                        if (it.equals(Firebase.auth.currentUser!!.uid)) {
                            db.collection("Videos").document(item.postId)
                                .update(
                                    "favorites",
                                    FieldValue.arrayRemove(Firebase.auth.currentUser!!.uid)
                                )
                            db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                                .update(
                                    "favorites",
                                    FieldValue.arrayRemove(item.postId)
                                )
                            UserSession.user.favorites.remove(item.postId)
                            favouriteIv.setImageResource(R.drawable.ic_heart_post)
                        } else {
                            db.collection("Videos").document(item.postId)
                                .update(
                                    "favorites",
                                    FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
                                )
                            db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                                .update(
                                    "favorites",
                                    FieldValue.arrayUnion(item.postId)
                                )
                            UserSession.user.favorites.add(item.postId)
                            favouriteIv.setImageResource(R.drawable.ic_heart_post_fill)
                        }
                    }
                }else{
                    db.collection("Videos").document(item.postId)
                        .update(
                            "favorites",
                            FieldValue.arrayUnion(Firebase.auth.currentUser!!.uid)
                        )
                    db.collection("Users").document(Firebase.auth.currentUser!!.uid)
                        .update(
                            "favorites",
                            FieldValue.arrayUnion(item.postId)
                        )
                    UserSession.user.favorites.add(item.postId)
                    favouriteIv.setImageResource(R.drawable.ic_heart_post_fill)
                }
            }

            root.setOnClickListener {
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

    }

    private fun convertMillisecondsToTimeFormat(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return if (hours > 0) {
            String.format("%02d:%02d", hours, minutes)
        } else if (minutes > 0) {
            String.format("%02d:%02d", minutes, remainingSeconds)
        } else {
            String.format("00:%02d", remainingSeconds)
        }
    }
}