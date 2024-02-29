package com.fajar.weathermap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.fajar.weathermap.data.adapter.SectionPagerAdapter
import com.fajar.weathermap.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity:AppCompatActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val viewPager: ViewPager2 = binding.viewPager


        val tabLayout: TabLayout = binding.tabs


        val sectionPagerAdapter = SectionPagerAdapter(this)
        viewPager.adapter = sectionPagerAdapter


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {

                0 -> tab.text = "Delhi"
                1 -> tab.text = "Melbourne"
                2 -> tab.text = "Mumbai"
                3 -> tab.text = "New York"
                4 -> tab.text = "Singapore"
                5 -> tab.text = "Sydney"
            }
        }.attach()
    }
}