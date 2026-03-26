package com.example.watermarkapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watermarkapp.databinding.ItemTextItemBinding
import com.example.watermarkapp.model.TextItem

/**
 * 文字项列表适配器
 */
class TextItemAdapter(
    private var items: MutableList<TextItem>,
    private val onItemChanged: (Int, TextItem) -> Unit,
    private val onItemDeleted: (Int) -> Unit
) : RecyclerView.Adapter<TextItemAdapter.TextItemViewHolder>() {

    class TextItemViewHolder(
        private val binding: ItemTextItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(
            item: TextItem,
            position: Int,
            onItemChanged: (Int, TextItem) -> Unit,
            onItemDeleted: (Int) -> Unit
        ) {
            // 设置初始值
            binding.etLabel.setText(item.label)
            binding.etContent.setText(item.content)
            binding.switchEnabled.isChecked = item.enabled
            
            // 监听标签变化
            binding.etLabel.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newItem = item.copy(
                        label = binding.etLabel.text.toString()
                    )
                    onItemChanged(position, newItem)
                }
            }
            
            // 监听内容变化
            binding.etContent.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    val newItem = item.copy(
                        content = binding.etContent.text.toString()
                    )
                    onItemChanged(position, newItem)
                }
            }
            
            // 监听开关变化
            binding.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
                val newItem = item.copy(enabled = isChecked)
                onItemChanged(position, newItem)
            }
            
            // 监听删除按钮
            binding.btnDelete.setOnClickListener {
                onItemDeleted(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val binding = ItemTextItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TextItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.bind(items[position], position, onItemChanged, onItemDeleted)
    }

    override fun getItemCount(): Int = items.size

    /**
     * 更新数据
     */
    fun updateData(newItems: MutableList<TextItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    /**
     * 添加项
     */
    fun addItem(item: TextItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    /**
     * 删除项
     */
    fun removeItem(position: Int) {
        if (position in 0 until items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, items.size)
        }
    }
}
