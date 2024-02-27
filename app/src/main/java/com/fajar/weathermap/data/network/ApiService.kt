package com.fajar.weathermap.data.network

import com.fajar.weathermap.data.response.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("weather")
    fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
            ): Call<WeatherResponse>

    @GET("weather")
    fun getWeatherLocation(
        @Query("q") location: String,
        @Query("appid") apiKey: String
    ): Call<WeatherResponse>

}