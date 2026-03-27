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
                    TextItem("project_name", "鸦岗保障性住房项目", true, "项目名称"),
                    TextItem("leader", "公司副总经理伊宝锋", true, "带班领导"),
                    TextItem("inspection", "安全生产领导带班检查及施工生产进度督导", true, "检查内容"),
                    TextItem("time", "2026.03.24 星期二", true, "拍摄时间"),
                    TextItem("location", "我在这里", true, "地点"),
                    TextItem("construction_unit", "广州珠江住房租赁发展投资有限公司", true, "建设单位"),
                    TextItem("supervision_unit", "广州建筑工程监理有限公司", true, "监理单位"),
                    TextItem("build_unit", "广州珠江建设发展有限公司", true, "施工单位")
                )
            )
        }
    }
}
