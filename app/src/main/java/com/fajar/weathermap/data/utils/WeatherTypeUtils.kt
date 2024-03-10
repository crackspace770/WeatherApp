package com.fajar.weathermap.data.utils

import com.fajar.weathermap.R

object WeatherTypeUtils {

    fun getWeatherColor(type: String): Int {
        return when(type) {

            "Clear" -> R.color.yellow_sun
            "Clouds" -> R.color.grayish_blue
            "Drizzle" -> R.color.blue
            "Haze" -> R.color.gray
            "Rain" -> R.color.blue
            "Smoke" -> R.color.gray
            "Mist" -> R.color.gray
            "Snow" -> R.color.gray
            else -> R.color.white
        }
    }

}