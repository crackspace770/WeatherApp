package com.fajar.weathermap.ui.newyork

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.weathermap.data.network.ApiConfig
import com.fajar.weathermap.data.response.WeatherResponse
import com.fajar.weathermap.data.utils.Constant.Companion.API_KEY
import com.fajar.weathermap.ui.sydney.SydneyViewModel
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherNYViewModel(private val context: Context): ViewModel() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "WeatherData",
        Context.MODE_PRIVATE
    )

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "WeatherNYViewModel"
        private const val WEATHER_DATA_KEY = "weatherny_data"
    }

    init {
        fetchWeatherData(40.7143, -74.006)
    }

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        val apiKey = API_KEY // Replace with your actual API key
        val client = ApiConfig.provideApiService().getWeather(latitude, longitude, apiKey)
        client.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                    saveWeatherData(response.body())
                } else {
                    loadWeatherData()
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                _isLoading.value = false
                loadWeatherData()
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    private fun saveWeatherData(weatherResponse: WeatherResponse?) {
        weatherResponse?.let {
            val editor = sharedPreferences.edit()
            val gson = Gson()
            val json = gson.toJson(weatherResponse)
            editor.putString(WEATHER_DATA_KEY, json)
            editor.apply()
        }
    }

    private fun loadWeatherData() {
        val json = sharedPreferences.getString(WEATHER_DATA_KEY, null)
        json?.let {
            val gson = Gson()
            val weatherResponse = gson.fromJson(json, WeatherResponse::class.java)
            _weatherData.value = weatherResponse
        }
    }

}
