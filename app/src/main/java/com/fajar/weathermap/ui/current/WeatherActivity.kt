package com.fajar.weathermap.ui.current

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fajar.weathermap.MainActivity
import com.fajar.weathermap.data.adapter.WeatherItemAdapter
import com.fajar.weathermap.data.utils.WeatherTypeUtils
import com.fajar.weathermap.data.utils.WeatherViewModelFactory
import com.fajar.weathermap.databinding.ActivityWeatherBinding


class WeatherActivity : AppCompatActivity(), LocationListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var locationManager: LocationManager
    private val weatherAdapter by lazy {WeatherItemAdapter()}

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, WeatherViewModelFactory(this))[WeatherViewModel::class.java]

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager



        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            )
        }

        viewModel.weatherData.observe(this) { weather ->

            val temperature = weather.main.temp.toInt().div(10)
            val feelLike = weather.main.feelsLike.toInt().div(10)
            val tempMax = weather.main.tempMax.toInt().div(10)
            val tempMin = weather.main.tempMin.toInt().div(10)
            val visibility = weather.visibility.toInt().div(100)

            binding.apply {
                tvMainTemp.text = "${temperature}°C"
                tvName.text = "${weather.name}, ${weather.sys.country}"
                tvTemp.text = "${temperature}°C/Feels like ${feelLike}°C"
                tvTempMaxMin.text = "${tempMax}° | ${tempMin}°"
                tvHumidity.text = "${weather.main.humidity}%"
                tvPressure.text = "${weather.main.pressure} mBar"
                tvWind.text = "${weather.wind.speed} mph"
                tvVisibility.text = "${visibility}%"
                tvRainChance.text = "${weather.clouds.all}%"


                backgroundMain.setBackgroundColor(ContextCompat.getColor(this@WeatherActivity, WeatherTypeUtils.getWeatherColor(weather.weather[0].main)))

                window.statusBarColor = ContextCompat.getColor(this@WeatherActivity, WeatherTypeUtils.getWeatherColor(weather.weather[0].main))


                if (weather != null) {
                    weatherAdapter.differ.submitList(weather.weather)
                }

                progressBar.visibility = View.GONE
            }


        }

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = View.VISIBLE
        }

        binding.tvOtherLocation.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setupRvWeather()
    }


    private fun setupRvWeather() {
        binding.rvWeather.apply {
            adapter = weatherAdapter
            layoutManager = LinearLayoutManager(this@WeatherActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onLocationChanged(location: Location) {
        locationManager.removeUpdates(this)

        val latitude = location.latitude
        val longitude = location.longitude

        viewModel.fetchWeatherData(latitude, longitude)
    }

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
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this
                )
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }



    companion object {
        const val LOCATION_PERMISSION_CODE = 1
        const val MIN_TIME_BW_UPDATES: Long = 2000 // 2 seconds
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 2f // meters

    }
}