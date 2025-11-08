package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

// 仓库层代码
object Respository {

    // 提供地址查询函数
    // liveData()提供挂起函数上下文,并将线程参数类型指定成Dispatchers.IO开启线程
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        // 调用封装的请求函数获取response
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        // 判断请求结果
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }

    // 刷新天气信息,一次获取实时天气信息和未来天气信息
    // liveData()提供挂起函数上下文,并将线程参数类型指定成Dispatchers.IO开启线程
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        // 创建协程作用域,便于使用async函数创建子协程异步请求两种天气信息
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            // 调用await()等待请求响应
            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()
            // 判断请求结果
            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(
                    realtimeResponse.result.realtime,
                    dailyResponse.result.daily
                )
                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    // 统一的try catch处理
    // 在函数类型前声明suspend关键字,表示传入的Lambda表达式中的代码拥有挂起函数上下文
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    // 保存地址
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}