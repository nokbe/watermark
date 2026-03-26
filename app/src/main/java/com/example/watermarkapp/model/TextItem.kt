package com.example.watermarkapp.model

/**
 * 水印文字项数据类
 */
data class TextItem(
    val key: String,              // 唯一标识
    var content: String,          // 文字内容
    var enabled: Boolean = true,  // 是否启用
    val label: String = ""        // 标签名称（用于显示）
) {
    // 转换为JSON字符串用于存储
    fun toJson(): String {
        return "$key|$content|$enabled|$label"
    }
    
    companion object {
        // 从JSON字符串解析
        fun fromJson(json: String): TextItem {
            val parts = json.split("|")
            return if (parts.size >= 4) {
                TextItem(
                    key = parts[0],
                    content = parts[1],
                    enabled = parts[2].toBoolean(),
                    label = parts[3]
                )
            } else {
                TextItem("", "", true, "")
            }
        }
    }
}
