package com.sunnyweather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.ui.weather.WeatherActivity

// 定义Place的适配器
class PlaceAdapter(
    private val fragment: PlaceFragment,
    private val placeList: List<Place>
):  // 继承RecyclerView的适配器,但泛型指定为Place适配器的ViewHolder
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    // 定义内部类ViewHolder来持有组件
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
    }

    // 在ViewHolder创建后加载子项布局
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        // 注册点击事件,点击启动WeatherActivity
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            val place = placeList[position]
            val activity = fragment.activity // 获取当前Fragment所处的Activity
            if (activity is WeatherActivity) {
                WeatherActivity().closeDrawer()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
            // 保存选中的地址
            fragment.viewModel.savePlace(place)
        }
        return holder
    }

    // 当ViewHolder绑定子项(子项滚入屏幕)时,为子项组件赋值
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 滚动的位置信息作为placeList的索引
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    // 统计需要的子项数量
    override fun getItemCount() = placeList.size

}