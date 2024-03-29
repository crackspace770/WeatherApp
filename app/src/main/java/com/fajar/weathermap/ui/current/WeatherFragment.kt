package com.fajar.weathermap.ui.current

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
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
import com.fajar.weathermap.data.utils.WeatherTypeUtils
import com.fajar.weathermap.data.utils.WeatherViewModelFactory
import com.fajar.weathermap.databinding.FragmentWeatherBinding
import com.google.android.material.tabs.TabLayout

class WeatherFragment: Fragment(), LocationListener {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var locationManager: LocationManager
    private lateinit var fragmentContext: Context
    private val weatherAdapter by lazy { WeatherItemAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(requireContext()))[WeatherViewModel::class.java]

        fragmentContext = requireContext()

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                WeatherActivity.LOCATION_PERMISSION_CODE
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                WeatherActivity.MIN_TIME_BW_UPDATES,
                WeatherActivity.MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
        }

        viewModel.weatherData.observe(requireActivity()) { weather ->

            val temperature = weather.main.temp.toInt().div(10)
            val feelLike = weather.main.feelsLike.toInt().div(10)
            val tempMax = weather.main.tempMax.toInt().div(10)
            val tempMin = weather.main.tempMin.toInt().div(10)
            val visibility = weather.visibility.toInt().div(100)

            binding.apply {
                tvWeatherMainTemp.text = "${temperature}°C"
                tvWeatherName.text = "${weather.name}, ${weather.sys.country}"
                tvWeatherTemp.text = "${temperature}°C/Feels like ${feelLike}°C"
                tvWeatherTempMaxMin.text = "${tempMax}° | ${tempMin}°"
                tvWeatherHumidity.text = "${weather.main.humidity}%"
                tvWeatherPressure.text = "${weather.main.pressure} mBar"
                tvWeatherWind.text = "${weather.wind.speed} mph"
                tvWeatherVisibility.text = "${visibility}%"
                tvWeatherRainChance.text = "${weather.clouds.all}%"


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
        binding.rvWeather.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onLocationChanged(location: Location) {
        locationManager.removeUpdates(this)

        val latitude = location.latitude
        val longitude = location.longitude

        viewModel.fetchWeatherData(latitude, longitude)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, request location updates
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this
                    )
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    companion object {
        private const val LOCATION_PERMISSION_CODE = 1
        private const val MIN_TIME_BW_UPDATES: Long = 2000// 2 seconds
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 2f // 2 meters

    }
}

