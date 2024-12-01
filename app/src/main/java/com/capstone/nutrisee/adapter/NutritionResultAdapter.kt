package com.capstone.nutrisee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R

class NutritionResultAdapter(private val results: List<Pair<String, Float>>) :
    RecyclerView.Adapter<NutritionResultAdapter.ResultViewHolder>() {

    class ResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nutrientName: TextView = itemView.findViewById(R.id.textNutrientName)
        val nutrientValue: TextView = itemView.findViewById(R.id.textNutrientValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_nutrition_result, parent, false)
        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = results[position]
        holder.nutrientName.text = result.first
        holder.nutrientValue.text = "${result.second} gr"
    }

    override fun getItemCount(): Int = results.size
}
