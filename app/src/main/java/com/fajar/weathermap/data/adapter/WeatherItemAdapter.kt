package com.fajar.weathermap.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.fajar.weathermap.R
import com.fajar.weathermap.data.response.Weather
import com.fajar.weathermap.databinding.WeatherItemBinding

class WeatherItemAdapter : RecyclerView.Adapter<WeatherItemAdapter.ListViewHolder>() {

    val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = WeatherItemBinding.bind(itemView)
        @SuppressLint("SetTextI18n")
        fun bind(data: Weather) {
            with(binding) {
                tvMain.text = data.main
                tvDescription.text = data.description


                // Set image based on weather condition
                imgWeather.setImageResource(getWeatherIcon(data.main))


            }
        }

        init {
            binding.root.setOnClickListener {
                // Handle item click here if needed
            }
        }
    }

    private fun getWeatherIcon(weatherCondition: String): Int {
        return when (weatherCondition) {
            "Clear" -> R.drawable.ic_sunny
            "Clouds" -> R.drawable.ic_cloud
            "Drizzle" -> R.drawable.ic_rainy
            "Haze" -> R.drawable.haze
            "Rain" -> R.drawable.ic_rainy
            "Smoke" -> R.drawable.haze
            "Mist" -> R.drawable.mist
            "Snow" -> R.drawable.snow
            else -> R.drawable.ic_block // Set a default icon or handle other conditions
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.weather_item, parent, false))
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val pokemon = differ.currentList[position] // Accessing the current list from differ
        holder.bind(pokemon)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size // Returning the size of the current list from differ
    }

    fun submitList(list: List<Weather>) {
        differ.submitList(list) // Submitting the list to differ for handling updates
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Weather>() {
            override fun areItemsTheSame(oldItem: Weather, newItem: Weather): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Weather, newItem: Weather): Boolean {
                return oldItem == newItem
            }
        }
    }
}