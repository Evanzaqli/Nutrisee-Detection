package com.capstone.nutrisee.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R
import com.capstone.nutrisee.database.ScanResult

class ScanHistoryAdapter(
    private val onDeleteClick: (ScanResult) -> Unit
) : ListAdapter<ScanResult, ScanHistoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ScanResult>() {
            override fun areItemsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ScanResult, newItem: ScanResult): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val foodName: TextView = view.findViewById(R.id.text_food_name)
        private val nutritionInfo: TextView = view.findViewById(R.id.text_calories)
        private val scanDate: TextView = view.findViewById(R.id.text_scan_date)
        private val deleteIcon: ImageView = view.findViewById(R.id.ic_delete)

        fun bind(scanResult: ScanResult) {
            foodName.text = scanResult.foodName
            nutritionInfo.text = scanResult.nutritionInfo
            scanDate.text = java.text.DateFormat.getDateTimeInstance().format(scanResult.scanDate)

            deleteIcon.setOnClickListener {
                onDeleteClick(scanResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
