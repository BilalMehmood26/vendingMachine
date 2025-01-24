package com.buzzware.vendingmachinetech.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.adapters.NotificationAdapter
import com.buzzware.vendingmachinetech.databinding.FragmentNotificationBinding
import com.buzzware.vendingmachinetech.model.Notification
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NotificationFragment : Fragment() {

    private lateinit var fragmentContext: Context
    private val binding : FragmentNotificationBinding by lazy {
        FragmentNotificationBinding.inflate(layoutInflater)
    }

    private val db =Firebase.firestore
    private val notificationList : ArrayList<Notification> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        getNotification()

        return binding.root
    }


    private fun getNotification(){
        binding.progressBar.visibility = View.VISIBLE
        db.collection("Notifications").addSnapshotListener { value, error ->
            if(error!=null){
                binding.progressBar.visibility = View.GONE
                Toast.makeText(fragmentContext, "${error.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            notificationList.clear()
            if(value!=null){
                binding.progressBar.visibility = View.GONE
                val notification = value.toObjects(Notification::class.java)
                notification.forEach {
                    if(it.userId.equals(Firebase.auth.currentUser!!.uid)){
                        notificationList.add(it)
                    }
                }
                setAdapter()
            }
        }
    }

    private fun setAdapter() {
        binding.notificationRV.layoutManager = LinearLayoutManager(fragmentContext)
        binding.notificationRV.adapter = NotificationAdapter(fragmentContext,notificationList)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }
}