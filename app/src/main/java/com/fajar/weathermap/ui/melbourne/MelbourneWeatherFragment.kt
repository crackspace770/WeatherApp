package com.fajar.weathermap.ui.melbourne

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.weathermap.R
import com.fajar.weathermap.data.adapter.WeatherItemAdapter
import com.fajar.weathermap.data.utils.WeatherTypeUtils
import com.fajar.weathermap.data.utils.WeatherViewModelFactory
import com.fajar.weathermap.databinding.FragmentMelbourneBinding
import com.google.android.material.tabs.TabLayout

class MelbourneWeatherFragment: Fragment() {

    private lateinit var binding: FragmentMelbourneBinding
    private lateinit var viewModel: MelbourneViewModel
    private val weatherAdapter by lazy { WeatherItemAdapter() }
    private lateinit var fragmentContext: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMelbourneBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(requireContext()))[MelbourneViewModel::class.java]

        fragmentContext = requireContext()

        viewModel.weatherData.observe(requireActivity()) { weather ->

            val temperature = weather.main.temp.toInt().div(10)
            val feelLike = weather.main.feelsLike.toInt().div(10)
            val tempMax = weather.main.tempMax.toInt().div(10)
            val tempMin = weather.main.tempMin.toInt().div(10)
            val visibility = weather.visibility.toInt().div(100)

            binding.apply {
                tvMelbourneMainTemp.text = "${temperature}°C"
                tvMelbourneName.text = "${weather.name}, ${weather.sys.country}"
                tvMelbourneTemp.text = "${temperature}°C/Feels like ${feelLike}°C"
                tvMelbourneTempMaxMin.text = "${tempMax}° | ${tempMin}°"
                tvMelbourneHumidity.text = "${weather.main.humidity}%"
                tvMelbournePressure.text = "${weather.main.pressure} mBar"
                tvMelbourneWind.text = "${weather.wind.speed} mph"
                tvMelbourneVisibility.text = "${visibility}%"
                tvMelbourneRainChance.text = "${weather.clouds.all}%"


                backgroundMain.setBackgroundColor(ContextCompat.getColor(requireContext(), WeatherTypeUtils.getWeatherColor(weather.weather[0].main)))
                activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), WeatherTypeUtils.getWeatherColor(weather.weather[0].main))

                activity?.let { mainActivity ->
                    mainActivity.findViewById<TabLayout>(R.id.tabs)?.apply {
                        for (i in 0 until tabCount) {
                            getTabAt(i)?.view?.background = ColorDrawable(ContextCompat.getColor(requireContext(), WeatherTypeUtils.getWeatherColor(weather.weather[0].main)))
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


    private fun setupRvWeather() {
        binding.rvMelbourneWeather.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }



}