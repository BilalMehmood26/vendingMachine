package com.buzzware.vendingmachinetech.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Video
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.CategoryAdapter
import com.buzzware.vendingmachinetech.adapters.HomeCategoryAdapter
import com.buzzware.vendingmachinetech.adapters.VideosAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentHomeBinding
import com.buzzware.vendingmachinetech.model.Category
import com.buzzware.vendingmachinetech.model.Videos
import com.buzzware.vendingmachinetech.utils.UserSession
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {

    private val binding : FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    private lateinit var fragmentContext: Context
    private val categoryList :ArrayList<Category> = arrayListOf()
    private val videosList :ArrayList<Videos> = arrayListOf()
    private val filteredVideosList :ArrayList<Videos> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding.apply {
            dateTv.text = formatTimestampToDateString(System.currentTimeMillis())
            userNameTv.text = "Hello ${UserSession.user.userName}!"
            if(UserSession.user.image.isNotEmpty()){
                Glide.with(fragmentContext).load(UserSession.user.image).into(profileIv)
            }

            searchEt.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    filteredVideosList.clear()
                     videosList.forEach {
                         if(it.title.lowercase().contains(p0!!)) filteredVideosList.add(it)
                     }
                    setAdapter(filteredVideosList)
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

        }
        getCategoryList ()
        getVideos()

        return binding.root
    }

    private fun getVideos(){

        Firebase.firestore.collection("Videos").addSnapshotListener { value, error ->
            if (error != null) {
                Toast.makeText(fragmentContext, "$error", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.VISIBLE
                return@addSnapshotListener
            }

            binding.progressBar.visibility = View.GONE
            videosList.clear()
            value!!.forEach {
                val video = Videos(
                    title = it.getString("title") ?: "",
                    categoryId = it.getString("categoryId") ?: "",
                    description = it.getString("description") ?: "",
                    publishDate = it.getLong("publishDate") ?: 0,
                    duration = it.getLong("duration") ?: 0,
                    userId = it.getString("userId") ?: "",
                    postId = it.getString("postId") ?: "",
                    thumbnailImage = it.getString("thumbnailImage") ?: "",
                    videoLink = it.getString("videoLink") ?: "",
                    isFavorite = it.getBoolean("isFavorite") ?: false // Set the retrieved boolean value
                )

                videosList.add(video)
            }
            setAdapter(videosList)

        }
    }

    private fun getCategoryList (){
        Firebase.firestore.collection("Categories").addSnapshotListener { value, error ->

            if (error != null) {
                Toast.makeText(fragmentContext, "$error", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.VISIBLE
                return@addSnapshotListener
            }

            binding.progressBar.visibility = View.GONE
            categoryList.clear()
            value!!.forEach {
                val video = it.toObject(Category::class.java)
                categoryList.add(video)
            }

            setCategory()
        }
    }

    private fun setCategory() {
        binding.categoryRV.layoutManager = LinearLayoutManager(fragmentContext, LinearLayoutManager.HORIZONTAL, false)
        binding.categoryRV.adapter = HomeCategoryAdapter(fragmentContext, categoryList)
    }

    private fun setAdapter(videoList:ArrayList<Videos>) {
        binding.continueRV.layoutManager = LinearLayoutManager(fragmentContext)
        binding.continueRV.adapter = VideosAdapter(fragmentContext, videoList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTimestampToDateString(timestamp: Long): String {
        val localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM, yyyy")
        return localDateTime.format(formatter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}