package com.example.watermarkapp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.watermarkapp.model.TextItem
import com.example.watermarkapp.model.WatermarkConfig
import org.json.JSONArray
import org.json.JSONObject

/**
 * 水印配置数据仓库
 * 使用SharedPreferences持久化存储
 */
class WatermarkRepository private constructor(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "watermark_config"
        private const val KEY_TEXT_ITEMS = "text_items"
        
        @Volatile
        private var instance: WatermarkRepository? = null
        
        fun getInstance(context: Context): WatermarkRepository {
            return instance ?: synchronized(this) {
                instance ?: WatermarkRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
    
    /**
     * 保存水印配置
     */
    fun saveConfig(config: WatermarkConfig) {
        val jsonArray = JSONArray()
        config.textItems.forEach { item ->
            val jsonObject = JSONObject().apply {
                put("key", item.key)
                put("content", item.content)
                put("enabled", item.enabled)
                put("label", item.label)
            }
            jsonArray.put(jsonObject)
        }
        sharedPreferences.edit()
            .putString(KEY_TEXT_ITEMS, jsonArray.toString())
            .apply()
    }
    
    /**
     * 加载水印配置
     */
    fun loadConfig(): WatermarkConfig {
        val jsonString = sharedPreferences.getString(KEY_TEXT_ITEMS, null)
        
        return if (jsonString != null) {
            try {
                val jsonArray = JSONArray(jsonString)
                val textItems = mutableListOf<TextItem>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    textItems.add(
                        TextItem(
                            key = jsonObject.getString("key"),
                            content = jsonObject.getString("content"),
                            enabled = jsonObject.getBoolean("enabled"),
                            label = jsonObject.getString("label")
                        )
                    )
                }
                WatermarkConfig(textItems)
            } catch (e: Exception) {
                e.printStackTrace()
                WatermarkConfig.createDefault()
            }
        } else {
            WatermarkConfig.createDefault()
        }
    }
    
    /**
     * 清除配置
     */
    fun clearConfig() {
        sharedPreferences.edit().clear().apply()
    }
}
