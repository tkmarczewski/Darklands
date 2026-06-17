package com.grimreich.ui.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R

class EnemyStripAdapter(private val enemies: List<EnemyData>) : 
    RecyclerView.Adapter<EnemyStripAdapter.ViewHolder>() {

    data class EnemyData(val name: String, val hp: Int, val maxHp: Int)

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvEnemyName)
        val hpBar: ProgressBar = view.findViewById(R.id.pbEnemyHp)
        val hpText: TextView = view.findViewById(R.id.tvEnemyHp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_enemy_strip, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val enemy = enemies[position]
        holder.name.text = enemy.name
        holder.hpBar.max = enemy.maxHp
        holder.hpBar.progress = enemy.hp
        holder.hpText.text = "${enemy.hp}/${enemy.maxHp}"
    }

    override fun getItemCount() = enemies.size
}
