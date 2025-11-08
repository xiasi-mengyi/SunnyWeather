package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

// 按照返回的JSON格式数据来定义相应的数据模型(实时天气数据)
data class RealtimeResponse(val status: String, val result: Result) {

    data class Result(val realtime: Realtime)

    data class Realtime(val skycon: String,
                        val temperature: Float,
                        @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)

}