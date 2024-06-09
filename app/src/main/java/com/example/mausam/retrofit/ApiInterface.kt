package com.example.mausam.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.mausam.dataclassW.WeatherAppData

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city : String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ):Call<WeatherAppData>
}