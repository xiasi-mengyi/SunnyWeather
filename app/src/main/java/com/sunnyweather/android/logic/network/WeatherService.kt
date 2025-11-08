package com.sunnyweather.android.logic.network

import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DailyResponse
import com.sunnyweather.android.logic.model.RealtimeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Query

// 用于访问天气信息API的Retrofit接口
interface WeatherService {

    // 请求实时天气v2.6/J4ibV1NV5zIz2lRK/101.6656,39.2072/realtime
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng") lng: String, @Path("lat") lat: String):
            Call<RealtimeResponse>

    // 请求未来几天天气v2.6/J4ibV1NV5zIz2lRK/101.6656,39.2072/daily?dailysteps=3
    @GET("v2.6/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng") lng: String,
                        @Path("lat") lat: String, ):
            Call<DailyResponse>

}