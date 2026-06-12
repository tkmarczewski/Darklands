package com.grimreich.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R
import com.grimreich.grimreich.v1.Item

class InventoryAdapter(
    private var items: List<Item>,
    private val onItemClick: (Item) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivItemIcon)
        val name: TextView = view.findViewById(R.id.tvItemName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        
        // Map item type to icon
        val iconRes = when (item.type.lowercase()) {
            "weapon" -> R.drawable.ic_item_sword_1h
            "armor" -> R.drawable.ic_item_armor_leather
            "herb", "potion" -> R.drawable.ic_item_potion_hp
            else -> R.drawable.ic_item_spear
        }
        holder.icon.setImageResource(iconRes)
        
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Item>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
