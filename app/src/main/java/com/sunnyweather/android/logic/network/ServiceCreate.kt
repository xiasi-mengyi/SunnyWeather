package com.sunnyweather.android.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// 创建Retrofit构建器
object ServiceCreate {

    // 基础域名
    private const val BASE_URL = "https://api.caiyunapp.com/"

    // 构建请求接口的动态代理对象
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 提供两种创建动态代理的方法,一种常规,一种泛型实化
    fun <T> create(serviceClass: Class<T>): T= retrofit.create(serviceClass)
    // 第二种方法调用第一种方法
    inline fun <reified T> create(): T = create(T::class.java)
}