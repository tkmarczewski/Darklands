package com.grimreich.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.grimreich.R
import com.grimreich.core.Hero

class PartyAdapter(private val heroes: List<Hero>) : RecyclerView.Adapter<PartyAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvHeroName)
        val hpBar: ProgressBar = view.findViewById(R.id.pbHeroHP)
        val level: TextView = view.findViewById(R.id.tvHeroLevel)
        val career: TextView = view.findViewById(R.id.tvHeroCareer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_party_member, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hero = heroes[position]
        holder.name.text = hero.name
        holder.hpBar.progress = (hero.hp * 100 / hero.maxHp)
        holder.level.text = "Lv ${hero.level}"
        holder.career.text = hero.currentCareer?.name ?: "Bez profesji"
    }

    override fun getItemCount() = heroes.size
}
