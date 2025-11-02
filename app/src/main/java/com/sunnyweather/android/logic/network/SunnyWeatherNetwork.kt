package com.sunnyweather.android.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


// 封装所有网络请求API(运用协程)
object SunnyWeatherNetwork {

    // 创建动态代理对象
    private val placeService = ServiceCreate.create<PlaceService>()

    // 提供挂起函数返回请求结果
    suspend fun searchPlaces(query: String) = placeService.searchPlace(query).await()

    // 封装请求过程
    private suspend fun <T> Call<T>.await(): T {
        // 创建并挂起协程,在上层开启的线程中执行
        return suspendCoroutine { Continuation ->
            // 开始请求,初步并处理返回
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    // 恢复协程并返回内容
                    if (body != null) Continuation.resume(body)
                    else Continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    Continuation.resumeWithException(t)
                }
            })
        }
    }
}