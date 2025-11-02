package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class SunnyWeatherApplication : Application() {
    companion object {
        // 获取全局上下文
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
        // 令牌值
        const val TOKEN = "J4ibV1NV5zIz2lRK"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}