package com.example.watermarkapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watermarkapp.adapter.TextItemAdapter
import com.example.watermarkapp.databinding.ActivityEditWatermarkBinding
import com.example.watermarkapp.model.TextItem
import com.example.watermarkapp.model.WatermarkConfig
import com.example.watermarkapp.viewmodel.WatermarkViewModel
import java.util.UUID

class EditWatermarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditWatermarkBinding
    private val viewModel: WatermarkViewModel by viewModels()
    private lateinit var adapter: TextItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditWatermarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupUI() {
        // 返回按钮
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        
        // 保存配置按钮
        binding.btnSaveConfig.setOnClickListener {
            saveConfig()
        }
        
        // 添加按钮
        binding.btnAddItem.setOnClickListener {
            addNewItem()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = TextItemAdapter(
            items = mutableListOf(),
            onItemChanged = { position, item ->
                viewModel.updateTextItem(position, item)
            },
            onItemDeleted = { position ->
                viewModel.removeTextItem(position)
                adapter.removeItem(position)
            }
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditWatermarkActivity)
            adapter = this@EditWatermarkActivity.adapter
        }
    }
    
    private fun observeViewModel() {
        viewModel.config.observe(this) { config ->
            adapter.updateData(config.textItems)
        }
        
        viewModel.successMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
    }
    
    private fun addNewItem() {
        val newItem = TextItem(
            key = UUID.randomUUID().toString(),
            content = "",
            enabled = true,
            label = "新项目"
        )
        
        viewModel.addTextItem(newItem)
        adapter.addItem(newItem)
        
        // 滚动到底部
        binding.recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
    }
    
    private fun saveConfig() {
        val currentConfig = viewModel.config.value ?: return
        
        // 保存配置
        viewModel.saveConfig(WatermarkConfig(currentConfig.textItems))
        
        // 返回结果
        setResult(RESULT_OK)
        finish()
    }
}
