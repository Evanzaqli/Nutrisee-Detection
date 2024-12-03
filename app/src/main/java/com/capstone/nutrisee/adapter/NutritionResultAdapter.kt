package com.capstone.nutrisee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R

class NutritionResultAdapter(
    private val foodName: String,
    private val nutritionList: List<Pair<String, Float>>
) : RecyclerView.Adapter<NutritionResultAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val foodNameTextView: TextView = view.findViewById(R.id.textFoodName)
        val proteinValueTextView: TextView = view.findViewById(R.id.textProteinValue)
        val carboValueTextView: TextView = view.findViewById(R.id.textCarboValue)
        val fatValueTextView: TextView = view.findViewById(R.id.textFatValue)
        val fiberValueTextView: TextView = view.findViewById(R.id.textFiberValue)
        val caloriesValueTextView: TextView = view.findViewById(R.id.textCaloriesValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nutrition_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.foodNameTextView.text = foodName

        nutritionList.forEach { (name, value) ->
            when (name) {
                "Protein" -> holder.proteinValueTextView.text = "Protein: ${value} g"
                "Carbohydrate" -> holder.carboValueTextView.text = "Carbohydrate: ${value} g"
                "Fat" -> holder.fatValueTextView.text = "Fat: ${value} g"
                "Fiber" -> holder.fiberValueTextView.text = "Fiber: ${value} g"
                "Calories" -> holder.caloriesValueTextView.text = "Calories: ${value} kcal"
            }
        }
    }

    override fun getItemCount() = 1
}
