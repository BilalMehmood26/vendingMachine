package com.buzzware.vendingmachinetech.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivitySubscriptionBinding

class SubscriptionActivity : AppCompatActivity() {

    private val binding : ActivitySubscriptionBinding by lazy {
        ActivitySubscriptionBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setListener()

    }

    private fun setListener() {

        binding.backIV.setOnClickListener { onBackPressed() }

        binding.monthlyLayout.setOnClickListener {
            binding.monthlyRadio.setImageResource(R.drawable.ic_radio_check)
            binding.yearRadio.setImageResource(R.drawable.ic_radio_uncheck)
        }

        binding.yearLayout.setOnClickListener {
            binding.yearRadio.setImageResource(R.drawable.ic_radio_check)
            binding.monthlyRadio.setImageResource(R.drawable.ic_radio_uncheck)
        }

        binding.getStartBtn.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}