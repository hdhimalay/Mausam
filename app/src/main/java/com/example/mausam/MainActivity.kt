package com.example.mausam

import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mausam.dataclassW.WeatherAppData
import com.example.mausam.retrofit.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //https://api.openweathermap.org/data/2.5/weather?q=Delhi&appid={API key}
    //Api- 2d7540f22bc27598f1750895af4d5711

    private lateinit var temperature:TextView
    private lateinit var location:TextView
    private lateinit var tempminimum:TextView
    private lateinit var tempmaximum:TextView
    private lateinit var sky:TextView
    private lateinit var feelsLike:TextView
    private lateinit var updateTime:TextView
    private lateinit var locationSearch:LinearLayout
    private lateinit var sunrise:TextView
    private lateinit var sunset:TextView
    private lateinit var pressure:TextView
    private lateinit var humidity:TextView
    private lateinit var wind:TextView
    private lateinit var info:TextView
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        locationSearch=findViewById(R.id.location_layout)
        updateTime=findViewById(R.id.updateatTv)
        temperature=findViewById(R.id.tempTv)
        location=findViewById(R.id.locationTV)
        tempminimum=findViewById(R.id.min_temp)
        tempmaximum=findViewById(R.id.max_temp)
        sky=findViewById(R.id.skytype)
        feelsLike=findViewById(R.id.feels)

        sunrise=findViewById(R.id.sunriseTv)
        sunset=findViewById(R.id.sunsetTv)
        wind=findViewById(R.id.windTv)
        pressure=findViewById(R.id.pressureTv)
        humidity=findViewById(R.id.humidityTv)
        info=findViewById(R.id.infoTv)


        val currentTime=System.currentTimeMillis()
        val formattedTime=timeFormat.format(currentTime)

        locationSearch.setOnClickListener{
            showSearchDialog()

        }

        try {
            fetchWeatherData("Delhi")
            updateTime.text="Last Update: ${formattedTime}"
        }catch (e: Exception){
            Log.d("TAG",e.toString())
        }
    }

    private fun showSearchDialog() {
        val builder=AlertDialog.Builder(this)
        val inflater=this.layoutInflater
        val dialogView=inflater.inflate(R.layout.custom_search_dialog,null)
        builder.setView(dialogView)

        val searchView=dialogView.findViewById<SearchView>(R.id.search_view)
        val dialog=builder.create()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null){
                    fetchWeatherData(query)
                    dialog.dismiss()
                    val currentTime = System.currentTimeMillis()
                    val formattedTime = timeFormat.format(currentTime)
                    updateTime.text = "Last Update: ${formattedTime}"
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        dialog.show()

    }


    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response= retrofit.getWeatherData(city,"2d7540f22bc27598f1750895af4d5711", "metric")
        response.enqueue(object: Callback<WeatherAppData>{
            override fun onResponse(call: Call<WeatherAppData>, response: Response<WeatherAppData>) {
                val responseBody=response.body()
                if (responseBody!=null){
                    val temp=Math.round(responseBody.main.temp)
                    val feels=Math.round(responseBody.main.feels_like)
                    val tempMin=Math.round(responseBody.main.temp_min)
                    val tempMax=Math.round(responseBody.main.temp_max)
                    val name=responseBody.name
                    val type=responseBody.weather[0].description.capitalize(Locale.ROOT)

                    val sunRise=responseBody.sys.sunrise
                    val sunSet=responseBody.sys.sunset
                    val pressureTd=responseBody.main.pressure
                    val humidityTd=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed


                    temperature.text= "$temp°C"
                    feelsLike.text="Feels like: $feels°C"
                    location.text=name
                    tempminimum.text="Min. Temp: $tempMin°C"
                    tempmaximum.text="Max. Temp: $tempMax°C"
                    sky.text= type
                    sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunRise.toLong() * 1000))
                    sunset.text= SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunSet.toLong() * 1000))
                    wind.text = "${windSpeed} m/s"
                    pressure.text = "${pressureTd} hPa"
                    humidity.text = "${humidityTd} %"
                    info.text = "by Himalay"


                }
            }

            override fun onFailure(call: Call<WeatherAppData>, response: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
}