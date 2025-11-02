package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

// 仓库层代码
object Respository {

    // 提供地址查询函数
    // liveData()提供挂起函数上下文,并将线程参数类型指定成Dispatchers.IO开启线程
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try {
            // 调用封装的请求函数获取response
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            // 判断请求结果
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)
            } else {
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
        // 发出地址查询结果
        emit(result)
    }
}