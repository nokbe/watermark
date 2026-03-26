package com.example.watermarkapp.model

/**
 * 水印配置数据类
 */
data class WatermarkConfig(
    var textItems: MutableList<TextItem> = mutableListOf()
) {
    companion object {
        // 创建默认配置（参考图片中的内容）
        fun createDefault(): WatermarkConfig {
            return WatermarkConfig(
                textItems = mutableListOf(
                    TextItem("project_name", "某某工程建设项目", true, "项目名称"),
                    TextItem("leader", "张三", true, "带班领导"),
                    TextItem("inspection", "安全质量检查", true, "检查内容"),
                    TextItem("time", "2026年03月24日 11:57", true, "拍摄时间"),
                    TextItem("location", "项目现场", true, "地点"),
                    TextItem("construction_unit", "某某建设单位", true, "建设单位"),
                    TextItem("supervision_unit", "某某监理单位", true, "监理单位"),
                    TextItem("build_unit", "某某施工单位", true, "施工单位")
                )
            )
        }
    }
}
