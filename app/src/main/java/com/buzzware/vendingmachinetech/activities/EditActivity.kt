package com.buzzware.vendingmachinetech.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityEditBinding

class EditActivity : BaseActivity() {

    private val binding : ActivityEditBinding by lazy {
        ActivityEditBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}