package com.example.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.data.HeatmapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    val selectTimeRangeIndex: Int = 0,
    val selectOsIndex: Int = 0,
    val isHeatmapLoading:Boolean = false,
    val heatmapData: List<List<Int>> = emptyList(),

    val isKeyboardLoading: Boolean = false,
    val keyboardData: Map<String, Int>? = null
)
/**
 * MainViewModel 负责：
 * 1. 持有和管理UI状态 (MainUiState)。
 * 2. 暴露状态给UI层 (通过StateFlow)。
 * 3. 提供公共方法给UI层调用，以响应用户交互并更新状态。
 * 4. 执行业务逻辑
 *
 * 这种模式遵循“单向数据流” (Unidirectional Data Flow) 原则：
 * UI -> ViewModel (调用事件方法) -> ViewModel更新状态 -> 状态流向UI -> UI刷新
 */
class MainViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    init {
        uiState
            .map{Pair(it.selectOsIndex,it.selectTimeRangeIndex)}
            .distinctUntilChanged()
            .onEach { (osIndex,timeIndex)->
                fetchHeatmapData(osIndex,timeIndex)
                fetchKeyboardHeatmapData(osIndex, timeIndex)
            }
            .launchIn(viewModelScope)
    }

    private fun fetchHeatmapData(osIndex: Int, timeIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isHeatmapLoading = true) }
            val data = HeatmapRepository.getHeatmapData(timeIndex, osIndex)
            _uiState.update { it.copy(isHeatmapLoading = false, heatmapData = data) }
        }
    }

    private fun fetchKeyboardHeatmapData(osIndex: Int, timeIndex: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isKeyboardLoading = true) }
            val data = HeatmapRepository.getKeyboardHeatmapData(timeIndex, osIndex)
            _uiState.update { it.copy(isKeyboardLoading = false, keyboardData = data) }
        }
    }
    //用户点击时间范围时调用
    fun onTimeRangeSelected(index: Int){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectTimeRangeIndex = index)
            }
        }
    }

    //用户点击操作系统时调用
    fun onOsSelected(index: Int){
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(selectOsIndex = index)
            }
        }
    }
}