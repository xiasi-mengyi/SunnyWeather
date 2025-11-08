package com.sunnyweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sunnyweather.android.logic.Respository
import com.sunnyweather.android.logic.model.Location
import kotlinx.coroutines.flow.flatMapLatest

class WeatherViewModel : ViewModel() {

    // 稍后搜索关键词的 LiveData（源数据）
    // 仓库层返回的LiveData对象不建议直接观察
    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherLiveData = locationLiveData
        // 将 LiveData 转换为 Flow（监听 locationLiveData 的变化）
        .asFlow()
        // 用 flatMapLatest 替代 switchMap：
        // 当 location 变化时，自动取消上一次的仓库请求，只处理最新的 location
        .flatMapLatest { location ->
            // 调用仓库层的 refreshWeather（返回 LiveData），并转换为 Flow
            Respository.refreshWeather(location.lng, location.lat).asFlow()
        }
        // 将 Flow 转换为 LiveData，供 UI 层观察（自动绑定 ViewModel 生命周期）
        .asLiveData(viewModelScope.coroutineContext)


    // 对外提供的刷新方法（更新源 LiveData 的值）
    fun refreshWeather(lng: String, lat: String) {
        // 先将传入的地址参数赋值给locationLiveData对象
        locationLiveData.value = Location(lng, lat)
    }

}