package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

// 地址请求响应数据模型
data class PlaceResponse(val status: String, val places: List<Place>)
// 相关地址信息
data class Place(val name: String, val location: Location,
    @SerializedName("formatted_address") val address: String)
// 地址经纬度
data class Location(val lng: String, val lat: String)
