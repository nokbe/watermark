# 照片水印工具 - 专业建筑项目水印应用

一个专为建筑行业设计的照片水印应用，支持自定义水印文字、样式固定、自动按比例添加水印到照片。

## 功能特性

### 核心功能
- **智能水印添加**：自动计算水印尺寸，按照片比例（宽度35%，高度45%）添加水印
- **固定样式**：蓝色渐变背景 + Logo + 文字区域，符合建筑行业标准
- **可配置文字**：支持增删改查水印文字项，每项可单独启用/禁用
- **实时预览**：选择照片后自动应用水印，即时查看效果
- **一键保存**：保存带水印的照片到系统相册

### 默认水印内容
- 项目名称
- 带班领导
- 检查内容
- 拍摄时间
- 地点
- 建设单位
- 监理单位
- 施工单位

## 技术架构

### 架构模式
- **MVVM架构**：View - ViewModel - Model分层设计
- **数据绑定**：LiveData + ViewModel实现数据观察
- **持久化存储**：SharedPreferences保存配置

### 核心模块

#### 1. 数据层（Model）
- `TextItem.kt` - 水印文字项数据模型
- `WatermarkConfig.kt` - 水印配置数据模型
- `WatermarkRepository.kt` - 配置数据仓库

#### 2. 业务逻辑层（ViewModel）
- `WatermarkViewModel.kt` - 处理所有业务逻辑

#### 3. 视图层（View）
- `MainActivity.kt` - 主界面，照片选择和保存
- `EditWatermarkActivity.kt` - 编辑水印界面
- `TextItemAdapter.kt` - 文字项列表适配器

#### 4. 工具类
- `WatermarkRenderer.kt` - 水印绘制核心逻辑

## 技术细节

### 水印比例控制
```kotlin
// 水印宽度占照片宽度的比例
WATERMARK_WIDTH_RATIO = 0.35f

// 水印高度占照片高度的比例
WATERMARK_HEIGHT_RATIO = 0.45f
```

### 水印绘制流程
1. 创建蓝色渐变背景（深蓝→蓝色→浅蓝）
2. 绘制Logo（默认"珠江建设"）
3. 遍历启用的文字项，按固定位置绘制
4. 叠加到原图左下角位置

### 透明度设置
- 水印整体透明度：85%（220/255）
- 文字带阴影效果，确保清晰可见

## 使用方法

### 主流程
1. 打开应用
2. 点击"选取照片"按钮
3. 从相册选择照片
4. 自动应用当前配置的水印
5. 预览效果
6. 点击"保存照片"保存到相册

### 编辑水印
1. 点击主界面的"编辑水印"按钮
2. 进入编辑界面
3. 修改文字项的内容、标签
4. 启用/禁用各项
5. 点击"新增文字项"添加自定义项
6. 点击"删除"移除不需要的项
7. 点击"保存"保存配置

## 权限说明

### Android 12及以下
- `READ_EXTERNAL_STORAGE` - 读取照片
- `WRITE_EXTERNAL_STORAGE` - 保存照片

### Android 13及以上
- `READ_MEDIA_IMAGES` - 读取照片
- 使用MediaStore API保存，无需写入权限

## 项目结构

```
app/src/main/
├── java/com/example/watermarkapp/
│   ├── model/                    # 数据模型
│   │   ├── TextItem.kt
│   │   └── WatermarkConfig.kt
│   ├── repository/               # 数据仓库
│   │   └── WatermarkRepository.kt
│   ├── viewmodel/                # 视图模型
│   │   └── WatermarkViewModel.kt
│   ├── adapter/                  # 适配器
│   │   └── TextItemAdapter.kt
│   ├── util/                     # 工具类
│   │   └── WatermarkRenderer.kt
│   ├── MainActivity.kt           # 主Activity
│   └── EditWatermarkActivity.kt  # 编辑Activity
│
├── res/
│   ├── layout/                   # 布局文件
│   │   ├── activity_main.xml
│   │   ├── activity_edit_watermark.xml
│   │   └── item_text_item.xml
│   ├── drawable/                 # 图标资源
│   │   ├── ic_logo.xml          # Logo图标
│   │   ├── ic_photo.xml
│   │   ├── ic_edit.xml
│   │   └── ...
│   ├── values/                   # 资源值
│   │   ├── colors.xml
│   │   ├── strings.xml
│   │   └── themes.xml
│   └── mipmap-*/                 # 应用图标
│
└── AndroidManifest.xml           # 清单文件
```

## 依赖库

- **AndroidX Core KTX** - Android KTX扩展
- **Material Components** - Material Design组件
- **Lifecycle** - ViewModel和LiveData
- **Activity KTX** - Activity扩展
- **RecyclerView** - 列表展示
- **CardView** - 卡片布局
- **Coroutines** - 协程支持

## 编译运行

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Kotlin 1.9.20
- Gradle 8.2.0
- Compile SDK 34
- Min SDK 24（Android 7.0）
- Target SDK 34

### 编译步骤
1. 使用Android Studio打开项目
2. 等待Gradle同步完成
3. 连接Android设备或启动模拟器
4. 点击运行按钮

## 自定义配置

### 修改水印比例
在 `WatermarkRenderer.kt` 中修改：
```kotlin
private const val WATERMARK_WIDTH_RATIO = 0.35f
private const val WATERMARK_HEIGHT_RATIO = 0.45f
```

### 修改水印颜色
在 `colors.xml` 中修改：
```xml
<color name="watermark_blue_dark">#1E3A8A</color>
<color name="watermark_blue">#3B82F6</color>
<color name="watermark_blue_light">#60A5FA</color>
```

### 自定义Logo
替换 `res/drawable/ic_logo.xml` 文件

### 添加自定义字体
1. 将字体文件放入 `assets/fonts/` 目录
2. 在 `WatermarkRenderer.kt` 中加载字体：
```kotlin
val typeface = Typeface.createFromAsset(context.assets, "fonts/your_font.ttf")
textPaint.typeface = typeface
```

## 版本历史

### v1.0.0 (2026-03-12)
- ✅ 初始版本发布
- ✅ 实现基础水印功能
- ✅ 支持文字项配置
- ✅ MVVM架构
- ✅ 自动按比例添加水印

## 许可证

Copyright 2026 WatermarkApp

Licensed under the Apache License, Version 2.0
