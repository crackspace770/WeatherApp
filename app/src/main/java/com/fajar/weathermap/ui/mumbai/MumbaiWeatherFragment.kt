package com.fajar.weathermap.ui.mumbai

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.weathermap.R
import com.fajar.weathermap.data.adapter.WeatherItemAdapter
import com.fajar.weathermap.data.utils.WeatherViewModelFactory
import com.fajar.weathermap.databinding.FragmentMumbaiBinding
import com.fajar.weathermap.ui.current.WeatherActivity
import com.fajar.weathermap.ui.delhi.DelhiViewModel
import com.google.android.material.tabs.TabLayout

class MumbaiWeatherFragment: Fragment() {

    private lateinit var binding: FragmentMumbaiBinding
    private lateinit var viewModel: MumbaiViewModel
    private val weatherAdapter by lazy { WeatherItemAdapter() }
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMumbaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(requireContext()))[MumbaiViewModel::class.java]

        fragmentContext = requireContext()

        viewModel.weatherData.observe(requireActivity()) { weather ->

            val temperature = weather.main.temp.toInt().div(10)
            val feelLike = weather.main.feelsLike.toInt().div(10)
            val tempMax = weather.main.tempMax.toInt().div(10)
            val tempMin = weather.main.tempMin.toInt().div(10)
            val visibility = weather.visibility.toInt().div(100)

            binding.apply {
                tvMumbaiMainTemp.text = "${temperature}°C"
                tvMumbaiName.text = "${weather.name}, ${weather.sys.country}"
                tvMumbaiTemp.text = "${temperature}°C/Feels like ${feelLike}°C"
                tvMumbaiTempMaxMin.text = "${tempMax}° | ${tempMin}°"
                tvMumbaiHumidity.text = "${weather.main.humidity}%"
                tvMumbaiPressure.text = "${weather.main.pressure} mBar"
                tvMumbaiWind.text = "${weather.wind.speed} mph"
                tvMumbaiVisibility.text = "${visibility}%"
                tvMumbaiRainChance.text = "${weather.clouds.all}%"


                backgroundMain.setBackgroundColor(getBackgroundColor(weather.weather[0].main))

                activity?.window?.statusBarColor = getBackgroundColor(weather.weather[0].main)

                activity?.let { mainActivity ->
                    mainActivity.findViewById<TabLayout>(R.id.tabs)?.apply {
                        for (i in 0 until tabCount) {
                            getTabAt(i)?.view?.background = getTabBackgroundColor(weather.weather[0].main)
                        }
                    }
                }

                if (weather != null) {
                    weatherAdapter.differ.submitList(weather.weather)
                }

            }


        }

        viewModel.isLoading.observe(requireActivity()) { isLoading->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }


        setupRvWeather()

    }

    private fun getBackgroundColor(weatherCondition: String): Int {
        return when (weatherCondition) {
            "Clear" -> ContextCompat.getColor(requireContext(), R.color.yellow_sun)
            "Clouds" -> ContextCompat.getColor(requireContext(), R.color.grayish_blue)
            "Drizzle" -> ContextCompat.getColor(requireContext(), R.color.blue)
            "Haze" -> ContextCompat.getColor(requireContext(), R.color.gray)
            "Rain" -> ContextCompat.getColor(requireContext(), R.color.blue)
            "Smoke" -> ContextCompat.getColor(requireContext(), R.color.gray)
            "Mist" -> ContextCompat.getColor(requireContext(), R.color.gray)
            "Snow" -> ContextCompat.getColor(requireContext(), R.color.grayish_blue)
            else -> ContextCompat.getColor(requireContext(), android.R.color.white) // Default color
        }
    }

    private fun getTabBackgroundColor(weatherCondition: String): Drawable {
        return when (weatherCondition) {
            "Clear" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.yellow_sun))
            "Clouds" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.grayish_blue))
            "Drizzle" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.blue))
            "Haze" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gray))
            "Rain" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.blue))
            "Smoke" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gray))
            "Mist" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.gray))
            "Snow" -> ColorDrawable(ContextCompat.getColor(requireContext(), R.color.grayish_blue))
            else -> ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.white)) // Default color
        }
    }


    private fun setupRvWeather() {
        binding.rvMumbaiWeather.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }



}