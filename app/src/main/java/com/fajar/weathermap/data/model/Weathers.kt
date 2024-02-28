package com.fajar.weathermap.data.model

import com.fajar.weathermap.data.response.Clouds
import com.fajar.weathermap.data.response.Coord
import com.fajar.weathermap.data.response.Main
import com.fajar.weathermap.data.response.Sys
import com.fajar.weathermap.data.response.Weather
import com.fajar.weathermap.data.response.Wind

data class Weathers (

    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long

)