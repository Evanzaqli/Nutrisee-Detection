package com.capstone.nutrisee.view

import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R
import com.capstone.nutrisee.adapter.NutritionResultAdapter
import com.capstone.nutrisee.helper.ImageClassifierHelper

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }

        if (imageUri != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                findViewById<ImageView>(R.id.imageResult).setImageBitmap(bitmap)

                val classifier = ImageClassifierHelper(this)
                val nutritionResult = classifier.analyzeFood(bitmap)

                val foodName = "Detect Result"
                val resultsList = listOf(
                    "Protein" to nutritionResult.protein,
                    "Carbohydrate" to nutritionResult.carbohydrate,
                    "Fat" to nutritionResult.fat,
                    "Fiber" to nutritionResult.fiber,
                    "Calories" to nutritionResult.calories
                )

                val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNutrients)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = NutritionResultAdapter(foodName, resultsList)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to process image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        }
    }
}
