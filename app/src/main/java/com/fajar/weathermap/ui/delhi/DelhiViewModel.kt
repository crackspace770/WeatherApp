package com.fajar.weathermap.ui.delhi

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fajar.weathermap.data.network.ApiConfig
import com.fajar.weathermap.data.response.WeatherResponse
import com.fajar.weathermap.data.utils.Constant.Companion.API_KEY
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DelhiViewModel(private val context: Context) : ViewModel() {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "WeatherData",
        Context.MODE_PRIVATE
    )

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "DelhiViewModel"
        private const val WEATHER_DATA_KEY = "delhi_data"
    }

    init {
        fetchWeatherData(28.6667, 77.2167)
    }

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        val apiKey = API_KEY
        val client = ApiConfig.provideApiService().getWeather(latitude, longitude, apiKey)
        client.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _weatherData.value = response.body()
                    saveWeatherData(response.body())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    loadWeatherData()
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