package com.buzzware.vendingmachinetech.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.buzzware.vendingmachinetech.R
import com.buzzware.vendingmachinetech.databinding.ActivityDashBoardBinding
import com.buzzware.vendingmachinetech.fragments.CategoryFragment
import com.buzzware.vendingmachinetech.fragments.HomeFragment
import com.buzzware.vendingmachinetech.fragments.NotificationFragment
import com.buzzware.vendingmachinetech.fragments.SettingFragment

class DashBoardActivity : BaseActivity() {

    private val binding : ActivityDashBoardBinding by lazy {
        ActivityDashBoardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setStatusBarColor(R.color.dark_bg)

        setHomeTab()
        setListener()

    }

    private fun setListener() {

        binding.homeTab.setOnClickListener {
            setHomeTab()
        }

        binding.categoryTab.setOnClickListener {
            setCategoryTab()
        }


        binding.notificationTab.setOnClickListener {
            setNotificationTab()
        }

        binding.settingTab.setOnClickListener {
            setSettingTab()
        }

    }

    private fun setHomeTab() {
        binding.homeTab.setImageResource(R.drawable.bottom_home_selected)
        binding.categoryTab.setImageResource(R.drawable.bottom_category_null)
        binding.notificationTab.setImageResource(R.drawable.bottom_notification_null)
        binding.settingTab.setImageResource(R.drawable.bottom_setting_null)

        loadFragment(HomeFragment())
    }

    private fun setCategoryTab() {
        binding.homeTab.setImageResource(R.drawable.bottom_home_null)
        binding.categoryTab.setImageResource(R.drawable.bottom_category_selected)
        binding.notificationTab.setImageResource(R.drawable.bottom_notification_null)
        binding.settingTab.setImageResource(R.drawable.bottom_setting_null)

        loadFragment(CategoryFragment())
    }

    private fun setNotificationTab() {
        binding.homeTab.setImageResource(R.drawable.bottom_home_null)
        binding.categoryTab.setImageResource(R.drawable.bottom_category_null)
        binding.notificationTab.setImageResource(R.drawable.bottom_notification_selected)
        binding.settingTab.setImageResource(R.drawable.bottom_setting_null)

        loadFragment(NotificationFragment())
    }

    private fun setSettingTab() {
        binding.homeTab.setImageResource(R.drawable.bottom_home_null)
        binding.categoryTab.setImageResource(R.drawable.bottom_category_null)
        binding.notificationTab.setImageResource(R.drawable.bottom_notification_null)
        binding.settingTab.setImageResource(R.drawable.bottom_setting_selected)

        loadFragment(SettingFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(androidx.appcompat.R.anim.abc_fade_in, com.google.android.material.R.anim.abc_fade_out)
        transaction.replace(binding.container.id, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in, androidx.appcompat.R.anim.abc_fade_out)
    }
}