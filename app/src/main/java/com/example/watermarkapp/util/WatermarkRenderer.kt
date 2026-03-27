package com.example.watermarkapp.util

import android.content.Context
import android.graphics.*
import com.example.watermarkapp.R
import com.example.watermarkapp.model.WatermarkConfig

/**
 * 水印绘制工具类
 */
class WatermarkRenderer(private val context: Context) {
    
    companion object {
        // 水印占比配置（参考图片分析）
        private const val WATERMARK_WIDTH_RATIO = 0.35f     // 水印宽度占照片宽度的比例
        private const val WATERMARK_HEIGHT_RATIO = 0.45f    // 水印高度占照片高度的比例
        private const val WATERMARK_OPACITY = 220           // 水印透明度 (0-255, 约85%不透明度)
        private const val WATERMARK_PADDING = 0.05f         // 水印内边距占水印高度的比例
        
        // 字体大小配置
        private const val TITLE_TEXT_SIZE_RATIO = 0.08f     // 标题字体大小占水印高度的比例
        private const val NORMAL_TEXT_SIZE_RATIO = 0.055f   // 正文字体大小占水印高度的比例
        private const val SMALL_TEXT_SIZE_RATIO = 0.04f     // 小字体大小占水印高度的比例
        
        // Logo配置
        private const val LOGO_HEIGHT_RATIO = 0.25f         // Logo高度占水印高度的比例
    }
    
    /**
     * 应用水印到图片
     */
    fun applyWatermark(originalBitmap: Bitmap, config: WatermarkConfig): Bitmap {
        // 创建可变副本
        val resultBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        
        // 计算水印尺寸
        val photoWidth = originalBitmap.width
        val photoHeight = originalBitmap.height
        val watermarkWidth = (photoWidth * WATERMARK_WIDTH_RATIO).toInt()
        val watermarkHeight = (photoHeight * WATERMARK_HEIGHT_RATIO).toInt()
        
        // 创建水印Bitmap
        val watermarkBitmap = createWatermarkBitmap(watermarkWidth, watermarkHeight, config)
        
        // 设置水印透明度
        val paint = Paint().apply {
            alpha = WATERMARK_OPACITY
        }
        
        // 计算水印位置（左下角）
        val left = 0f
        val top = (photoHeight - watermarkHeight).toFloat()
        
        // 绘制水印到原图
        canvas.drawBitmap(watermarkBitmap, left, top, paint)
        
        // 回收水印Bitmap
        watermarkBitmap.recycle()
        
        return resultBitmap
    }
    
    /**
     * 创建水印Bitmap
     */
    private fun createWatermarkBitmap(width: Int, height: Int, config: WatermarkConfig): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        // 绘制渐变背景
        drawGradientBackground(canvas, width, height)
        
        // 绘制Logo
        drawLogo(canvas, width, height)
        
        // 绘制文字内容
        drawTextContent(canvas, width, height, config)
        
        return bitmap
    }
    
    /**
     * 绘制渐变背景
     */
    private fun drawGradientBackground(canvas: Canvas, width: Int, height: Int) {
        // 创建蓝色渐变（从深蓝到浅蓝，参考图片效果）
        val gradient = LinearGradient(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            intArrayOf(
                Color.parseColor("#1E3A8A"),  // 深蓝色
                Color.parseColor("#3B82F6"),  // 蓝色
                Color.parseColor("#60A5FA")   // 浅蓝色
            ),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
        
        val paint = Paint().apply {
            shader = gradient
        }
        
        // 绘制圆角矩形背景
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        val cornerRadius = 8f
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        
        // 添加边框
        val borderPaint = Paint().apply {
            color = Color.parseColor("#1E40AF")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 100
        }
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)
    }
    
    /**
     * 绘制Logo
     */
    private fun drawLogo(canvas: Canvas, width: Int, height: Int) {
        try {
            // 从资源加载Logo
            val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_logo)
            if (logoBitmap != null) {
                // 计算Logo尺寸和位置（左上角）
                val logoHeight = (height * LOGO_HEIGHT_RATIO).toInt()
                val logoWidth = (logoBitmap.width * logoHeight / logoBitmap.height.toFloat()).toInt()
                val padding = (height * WATERMARK_PADDING).toFloat()

                // 缩放Logo
                val scaledLogo = Bitmap.createScaledBitmap(logoBitmap, logoWidth, logoHeight, true)

                // 绘制Logo（左上角）
                val left = padding
                val top = padding
                canvas.drawBitmap(scaledLogo, left, top, null)

                // 回收Bitmap
                scaledLogo.recycle()
                logoBitmap.recycle()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果Logo加载失败，绘制文字Logo
            drawTextLogo(canvas, width, height)
        }
    }
    
    /**
     * 绘制文字Logo（备用方案）
     */
    private fun drawTextLogo(canvas: Canvas, width: Int, height: Int) {
        val padding = (height * WATERMARK_PADDING).toFloat()
        val textSize = (height * 0.1f)
        
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = textSize
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            setShadowLayer(2f, 1f, 1f, Color.BLACK)
        }
        
        canvas.drawText("珠江建设", width / 2f, padding + textSize, paint)
    }
    
    /**
     * 绘制文字内容
     */
    private fun drawTextContent(canvas: Canvas, width: Int, height: Int, config: WatermarkConfig) {
        val padding = (height * WATERMARK_PADDING).toFloat()
        val logoHeight = (height * LOGO_HEIGHT_RATIO)
        var currentY = padding + logoHeight + padding

        // 使用相同的基础字体大小，标签稍小
        val baseTextSize = (height * NORMAL_TEXT_SIZE_RATIO)
        val labelTextSize = baseTextSize * 0.9f
        val contentTextSize = baseTextSize
        val lineSpacing = contentTextSize * 1.6f

        // 创建标签画笔（浅灰色）
        val labelPaint = Paint().apply {
            color = Color.parseColor("#B8C5D6")  // 浅灰蓝色标签
            textSize = labelTextSize
            isAntiAlias = true
            textAlign = Paint.Align.LEFT
        }

        // 创建内容画笔（白色粗体带阴影）
        val contentPaint = Paint().apply {
            color = Color.WHITE
            textSize = contentTextSize
            isAntiAlias = true
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
            setShadowLayer(2f, 1f, 1f, Color.argb(100, 0, 0, 0))
        }

        // 绘制每个启用的文字项
        config.textItems.filter { it.enabled }.forEach { item ->
            // 绘制标签
            if (item.label.isNotEmpty()) {
                val fullLabel = "${item.label}："
                canvas.drawText(fullLabel, padding, currentY, labelPaint)

                // 计算内容起始位置
                val labelWidth = labelPaint.measureText(fullLabel)
                canvas.drawText(item.content, padding + labelWidth, currentY, contentPaint)
            } else {
                canvas.drawText(item.content, padding, currentY, contentPaint)
            }

            currentY += lineSpacing
        }
    }
}
