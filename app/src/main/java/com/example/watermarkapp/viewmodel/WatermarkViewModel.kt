package com.example.watermarkapp.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.watermarkapp.model.TextItem
import com.example.watermarkapp.model.WatermarkConfig
import com.example.watermarkapp.repository.WatermarkRepository
import com.example.watermarkapp.util.WatermarkRenderer

/**
 * 水印ViewModel
 */
class WatermarkViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = WatermarkRepository.getInstance(application)
    private val watermarkRenderer = WatermarkRenderer(application)
    
    // 配置数据
    private val _config = MutableLiveData<WatermarkConfig>()
    val config: LiveData<WatermarkConfig> = _config
    
    // 选中的图片Uri
    private val _selectedImageUri = MutableLiveData<Uri?>()
    val selectedImageUri: LiveData<Uri?> = _selectedImageUri
    
    // 原始图片
    private val _originalBitmap = MutableLiveData<Bitmap?>()
    val originalBitmap: LiveData<Bitmap?> = _originalBitmap
    
    // 添加水印后的图片
    private val _watermarkedBitmap = MutableLiveData<Bitmap?>()
    val watermarkedBitmap: LiveData<Bitmap?> = _watermarkedBitmap
    
    // 加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 成功信息
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    init {
        // 加载保存的配置
        loadConfig()
    }
    
    /**
     * 加载配置
     */
    fun loadConfig() {
        val savedConfig = repository.loadConfig()
        _config.value = savedConfig
    }
    
    /**
     * 保存配置
     */
    fun saveConfig(config: WatermarkConfig) {
        repository.saveConfig(config)
        _config.value = config
        _successMessage.value = "配置已保存"
    }
    
    /**
     * 设置选中的图片
     */
    fun setSelectedImage(uri: Uri?, bitmap: Bitmap?) {
        _selectedImageUri.value = uri
        _originalBitmap.value = bitmap
        _watermarkedBitmap.value = null
    }
    
    /**
     * 应用水印
     */
    fun applyWatermark() {
        val bitmap = _originalBitmap.value
        val config = _config.value
        
        if (bitmap == null) {
            _errorMessage.value = "请先选择图片"
            return
        }
        
        if (config == null) {
            _errorMessage.value = "配置未加载"
            return
        }
        
        _isLoading.value = true
        
        try {
            val watermarkedBitmap = watermarkRenderer.applyWatermark(bitmap, config)
            _watermarkedBitmap.value = watermarkedBitmap
            _successMessage.value = "水印已添加"
        } catch (e: Exception) {
            e.printStackTrace()
            _errorMessage.value = "添加水印失败: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    /**
     * 添加文字项
     */
    fun addTextItem(textItem: TextItem) {
        val currentConfig = _config.value ?: return
        currentConfig.textItems.add(textItem)
        _config.value = currentConfig
    }
    
    /**
     * 删除文字项
     */
    fun removeTextItem(position: Int) {
        val currentConfig = _config.value ?: return
        if (position in 0 until currentConfig.textItems.size) {
            currentConfig.textItems.removeAt(position)
            _config.value = currentConfig
        }
    }
    
    /**
     * 更新文字项
     */
    fun updateTextItem(position: Int, textItem: TextItem) {
        val currentConfig = _config.value ?: return
        if (position in 0 until currentConfig.textItems.size) {
            currentConfig.textItems[position] = textItem
            _config.value = currentConfig
        }
    }
    
    /**
     * 清除消息
     */
    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }
}
