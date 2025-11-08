package com.sunnyweather.android.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.databinding.FragmentPlaceBinding
import com.sunnyweather.android.ui.weather.WeatherActivity

class PlaceFragment: Fragment() {

    // 声明 Binding 变量（ nullable + lateinit，避免内存泄漏）
    private var _binding: FragmentPlaceBinding? = null
    // 对外暴露不可变的 Binding（仅在视图存在时可用）
    private val binding get() = _binding!!
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 初始化 Binding（attachToRoot 必须为 false，由系统处理视图添加）
        _binding = FragmentPlaceBinding.inflate(inflater, container, false)
        return binding.root // 返回布局根视图
    }

    @SuppressLint("NotifyDataSetChanged") // 忽略警告
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 对地址存储的状态进行判断和读取
        // 当PlaceFragment被嵌入MainActivity中(不在天气界面)，并且之前已经存在选中的城市,执行if
        if (activity is MainActivity && viewModel.isPlaceSaved()) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        // 给RecyclerView设置LayoutManager和适配器
        val layoutManager = LinearLayoutManager(activity)
        val adapter = PlaceAdapter(this, viewModel.placeList) // 使用PlaceViewModel中的placeList集合作为数据源
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        // 调用doAfterTextChanged()监听搜索框内容的变化情况
        binding.searchPlaceEdit.doAfterTextChanged { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                // 将搜索内容传递给PlaceViewModel的searchPlaces()方法
                // 发起搜索城市数据的网络请求
                viewModel.searchPlace(content)
            } else {
                // 搜索框中的内容为空时，隐藏RecyclerView，显示背景图
                binding.recyclerView.visibility = View.GONE
                binding.bgImageView.visibility = View.VISIBLE
                // 清空placeList集合,并通知PlaceAdapter刷新界面
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }

        // 对PlaceViewModel中的placeLiveData对象进行观察，
        // 当有任何数据变化时，就会回调到传入的Observer接口实现中。
        viewModel.placeLiveData.observe(viewLifecycleOwner) { result ->
            val places = result.getOrNull()
            // 会对回调的数据进行判断
            if (places != null) {
                // 回调数据不为空,显示RecyclerView，隐藏背景图
                binding.recyclerView.visibility = View.VISIBLE
                binding.bgImageView.visibility = View.GONE
                // 先清空placeList集合,避免意外
                viewModel.placeList.clear()
                // 将新数据添加到PlaceViewModel的placeList集合中,并通知PlaceAdapter刷新界面
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                // 回调数据为空，说明发生了异常
                Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }

    }

}