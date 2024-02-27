package com.fajar.weathermap.ui

import android.Manifest
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
import com.fajar.weathermap.data.adapter.WeatherItemAdapter
import com.fajar.weathermap.databinding.ActivityWeatherBinding

class WeatherActivity : AppCompatActivity(), LocationListener {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityWeatherBinding
    private lateinit var locationManager: LocationManager
    private val weatherAdapter by lazy {WeatherItemAdapter()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Request location updates
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

            binding.apply {
                tvBase.text = weather.main.temp.toString()
                tvName.text = weather.name

                if (weather != null) {
                    weatherAdapter.differ.submitList(weather.weather)
                }

                progressBar.visibility = View.GONE
            }


        }

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = View.VISIBLE
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
        private const val LOCATION_PERMISSION_CODE = 1
        private const val MIN_TIME_BW_UPDATES: Long = 5000 // 5 seconds
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 5f // 5 meters
    }
}