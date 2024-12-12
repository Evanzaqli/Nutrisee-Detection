package com.capstone.nutrisee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R
import com.capstone.nutrisee.data.model.NutritionInfo

class NutritionResultAdapter(
    private val nutritionInfoList: List<NutritionInfo>
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
        if (nutritionInfoList.isNotEmpty()) {
            val nutritionInfo = nutritionInfoList[position]

            holder.foodNameTextView.text = nutritionInfo.foodClass
            holder.proteinValueTextView.text = "Protein: ${nutritionInfo.protein} g"
            holder.carboValueTextView.text = "Carbohydrates: ${nutritionInfo.carbohydrates} g"
            holder.fatValueTextView.text = "Fat: ${nutritionInfo.fat} g"
            holder.fiberValueTextView.text = "Fiber: ${nutritionInfo.fiber} g"
            holder.caloriesValueTextView.text = "Calories: ${nutritionInfo.calories} kcal"
        }
    }

    override fun getItemCount() = nutritionInfoList.size
}
