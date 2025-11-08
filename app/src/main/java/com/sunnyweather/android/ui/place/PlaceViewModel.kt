package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sunnyweather.android.logic.Respository
import com.sunnyweather.android.logic.model.Place
import kotlinx.coroutines.flow.flatMapLatest


class PlaceViewModel: ViewModel() {

    // 稍后搜索关键词的 LiveData（源数据）
    // 仓库层返回的LiveData对象不建议直接观察
    private val searchLiveData = MutableLiveData<String>()

    // 暂时用不到
    val placeList = ArrayList<Place>()

    val placeLiveData = searchLiveData
        // 将 LiveData 转换为 Flow（监听 searchLiveData 的变化）
        .asFlow()
        // 用 flatMapLatest 替代 switchMap：
        // 当 query 变化时，自动取消上一次的仓库请求，只处理最新的 query
        .flatMapLatest { query ->
            // 调用仓库层的 searchPlaces（返回 LiveData），并转换为 Flow
            Respository.searchPlaces(query).asFlow()
        }
        // 将 Flow 转换为 LiveData，供 UI 层观察（自动绑定 ViewModel 生命周期）
        .asLiveData(viewModelScope.coroutineContext)

    // 对外提供的搜索方法（更新源 LiveData 的值）
    fun searchPlace(query: String) {
        // 先将传入的搜索参数赋值给searchLiveData对象
        // 避免重复请求（相同 query 不触发新请求）
        if (searchLiveData.value == query) return
        searchLiveData.value = query
    }

    // 保存地址
    fun savePlace(place: Place) = Respository.savePlace(place)
    // 对存储的地址进行读取
    fun getSavedPlace() = Respository.getSavedPlace()
    // 对地址存储的状态进行判断
    fun isPlaceSaved() = Respository.isPlaceSaved()

}