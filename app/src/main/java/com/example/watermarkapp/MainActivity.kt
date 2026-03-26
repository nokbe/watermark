package com.example.watermarkapp

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.watermarkapp.databinding.ActivityMainBinding
import com.example.watermarkapp.viewmodel.WatermarkViewModel
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WatermarkViewModel by viewModels()
    
    // 图片选择器
    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            loadImage(it)
        }
    }
    
    // 权限请求
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            saveImageToGallery()
        } else {
            Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show()
        }
    }
    
    // 编辑水印回调
    private val editWatermarkLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 配置已更新，重新加载
            viewModel.loadConfig()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // 选取照片按钮
        binding.btnSelectPhoto.setOnClickListener {
            selectImageLauncher.launch("image/*")
        }
        
        // 编辑水印按钮
        binding.btnEditWatermark.setOnClickListener {
            val intent = Intent(this, EditWatermarkActivity::class.java)
            editWatermarkLauncher.launch(intent)
        }
        
        // 保存按钮
        binding.btnSave.setOnClickListener {
            checkPermissionAndSave()
        }
    }
    
    private fun observeViewModel() {
        // 观察原始图片
        viewModel.originalBitmap.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.imageView.visibility = View.VISIBLE
                binding.tvPlaceholder.visibility = View.GONE
                binding.imageView.setImageBitmap(bitmap)
                binding.btnSave.isEnabled = false
            } else {
                binding.imageView.visibility = View.GONE
                binding.tvPlaceholder.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
            }
        }
        
        // 观察水印图片
        viewModel.watermarkedBitmap.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.imageView.setImageBitmap(bitmap)
                binding.btnSave.isEnabled = true
            }
        }
        
        // 观察加载状态
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        // 观察成功消息
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
        
        // 观察错误消息
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }
    
    private fun loadImage(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.setSelectedImage(uri, bitmap)
                
                // 自动应用水印
                viewModel.applyWatermark()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "加载图片失败: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun checkPermissionAndSave() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            // Android 9 及以下需要权限
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                saveImageToGallery()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        } else {
            // Android 10+ 不需要权限
            saveImageToGallery()
        }
    }
    
    private fun saveImageToGallery() {
        val bitmap = viewModel.watermarkedBitmap.value
        if (bitmap == null) {
            Toast.makeText(this, R.string.apply_watermark_first, Toast.LENGTH_SHORT).show()
            return
        }
        
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "watermark_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WatermarkApp")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        
        val contentResolver = applicationContext.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            try {
                val outputStream: OutputStream? = contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                    Toast.makeText(this, R.string.photo_saved, Toast.LENGTH_SHORT).show()
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        contentResolver.update(it, contentValues, null, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
