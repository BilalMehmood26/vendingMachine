package com.buzzware.vendingmachinetech.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.FavoriteAdapter
import com.buzzware.vendingmachinetech.adapters.VideosAdapter
import com.buzzware.vendingmachinetech.databinding.ActivityCategoryDetailBinding
import com.buzzware.vendingmachinetech.model.Videos
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryDetailActivity : BaseActivity() {

    private val binding: ActivityCategoryDetailBinding by lazy {
        ActivityCategoryDetailBinding.inflate(layoutInflater)
    }

    private var title = ""
    private var categoryID = ""
    private val videosList: ArrayList<Videos> = arrayListOf()
    private val filteredVideosList: ArrayList<Videos> = arrayListOf()
    private val favoriteList: ArrayList<Videos> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        title = intent.getStringExtra("title").toString()
        binding.textView6.text = title
        categoryID = intent.getStringExtra("categoryID").toString()
        getVideos()
        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

        binding.apply {

            searchEt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (!title.equals("Favourites")) {
                        filteredVideosList.clear()
                        videosList.forEach {
                            if (it.title.lowercase().contains(p0!!)) filteredVideosList.add(it)
                        }
                        setVideoAdapter(filteredVideosList)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }

    }

    private fun setVideoAdapter(videoList: ArrayList<Videos>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = VideosAdapter(this, videoList)
    }

    private fun setFavVideoAdapter(videoList: ArrayList<Videos>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = FavoriteAdapter(this, videoList){
            videoList.removeAt(it)
            binding.recyclerView.adapter!!.notifyDataSetChanged()
        }
    }


    private fun getVideos() {

        Firebase.firestore.collection("Videos").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(this, "$error", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.VISIBLE
                return@addSnapshotListener
            }

            binding.progressBar.visibility = View.GONE
            videosList.clear()
            favoriteList.clear()
            value!!.forEach {
                val categoryID = it.getString("categoryId") ?: ""
                val postId = it.getString("postId") ?: ""
                val videoDetails = it.toObject(Videos::class.java)
                val video = Videos(
                    title = videoDetails.title,
                    categoryId = categoryID,
                    description = videoDetails.description,
                    date = videoDetails.date,
                    duration = it.getLong("duration") ?: 0,
                    userId = it.getString("userId") ?: "",
                    postId = postId,
                    thumbnailImage = it.getString("thumbnailImage") ?: "",
                    videoLink = it.getString("videoLink") ?: "",
                    favorites = videoDetails.favorites
                )

                if (categoryID.equals(this.categoryID)) {
                    videosList.add(video)
                    setVideoAdapter(videosList)
                }else{
                    UserSession.user.favorites.forEach { favorite ->
                        if (favorite.equals(postId)) {
                            favoriteList.add(video)
                        }
                    }
                    setFavVideoAdapter(favoriteList)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            androidx.appcompat.R.anim.abc_fade_in,
            androidx.appcompat.R.anim.abc_fade_out
        )
    }
}