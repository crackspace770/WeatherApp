package com.fajar.weathermap.data.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.fajar.weathermap.ui.current.WeatherFragment
import com.fajar.weathermap.ui.delhi.DelhiWeatherFragment
import com.fajar.weathermap.ui.melbourne.MelbourneWeatherFragment
import com.fajar.weathermap.ui.mumbai.MumbaiWeatherFragment
import com.fajar.weathermap.ui.newyork.WeatherNYFragment
import com.fajar.weathermap.ui.singapore.SingaporeWeatherFragment
import com.fajar.weathermap.ui.sydney.SydneyWeatherFragment

class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = DelhiWeatherFragment()
            1 -> fragment = MelbourneWeatherFragment()
            2 -> fragment = MumbaiWeatherFragment()
            3 -> fragment = WeatherNYFragment()
            4 -> fragment = SingaporeWeatherFragment()
            5 -> fragment = SydneyWeatherFragment()

        }
        return fragment as Fragment
    }
}