package com.fajar.weathermap.data.utils

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fajar.weathermap.ui.current.WeatherViewModel
import com.fajar.weathermap.ui.delhi.DelhiViewModel
import com.fajar.weathermap.ui.melbourne.MelbourneViewModel
import com.fajar.weathermap.ui.mumbai.MumbaiViewModel
import com.fajar.weathermap.ui.newyork.WeatherNYViewModel
import com.fajar.weathermap.ui.singapore.SingaporeViewModel
import com.fajar.weathermap.ui.sydney.SydneyViewModel

class WeatherViewModelFactory(private val context: Context) : ViewModelProvider.AndroidViewModelFactory(
    context.applicationContext as Application
) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(DelhiViewModel::class.java)) {
            return DelhiViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(MelbourneViewModel::class.java)) {
            return MelbourneViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(MumbaiViewModel::class.java)) {
            return MumbaiViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(WeatherNYViewModel::class.java)) {
            return WeatherNYViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(SingaporeViewModel::class.java)) {
            return SingaporeViewModel(context) as T
        }
        if (modelClass.isAssignableFrom(SydneyViewModel::class.java)) {
            return SydneyViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}