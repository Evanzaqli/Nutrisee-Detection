package com.capstone.nutrisee.view

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.nutrisee.R
import com.capstone.nutrisee.adapter.NutritionResultAdapter
import com.capstone.nutrisee.data.model.NutritionInfo
import com.capstone.nutrisee.database.ScanResult
import com.capstone.nutrisee.database.ScanResultDatabase
import com.capstone.nutrisee.login.LoginActivity
import com.capstone.nutrisee.service.ApiConfig
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ResultActivity : AppCompatActivity() {

    private fun showLoading(isLoading: Boolean) {
        val progressBar = findViewById<ProgressBar>(R.id.progressBarLoading)
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageUri = intent.getStringExtra("image_uri")?.let { Uri.parse(it) }
        val token = getAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication token not found, please re-login.", Toast.LENGTH_SHORT).show()
            navigateToLogin()
            return
        }

        if (imageUri != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                findViewById<ImageView>(R.id.imageResult).setImageBitmap(bitmap)

                val tempFile = createTempFile(bitmap)

                callDetectFoodApi(tempFile, token)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to process image: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("ResultActivity", "Error loading image: ${e.message}", e)
            }
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createTempFile(bitmap: Bitmap): File {
        val tempFile = File(cacheDir, "temp_image.jpg")
        try {
            val fos = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()
            Log.d("ResultActivity", "Temporary file created: ${tempFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("ResultActivity", "Error creating temp file: ${e.message}", e)
        }
        return tempFile
    }

    private fun callDetectFoodApi(imageFile: File, token: String) {
        val requestBody = RequestBody.create("image/jpeg".toMediaType(), imageFile)
        val filePart = MultipartBody.Part.createFormData("file", imageFile.name, requestBody)

        lifecycleScope.launch {
            showLoading(true)
            try {
                val apiService = ApiConfig.getDetectFoodApiService()
                val response = withContext(Dispatchers.IO) {
                    apiService.detectFood(filePart, "Bearer $token")
                }

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        val detectedFoods = responseData.detectedFoods
                        val nutritionInfo = responseData.nutritionInfo

                        // Simpan hasil scan ke database
                        saveScanResultToDatabase(detectedFoods.joinToString(", "), nutritionInfo)

                        // Tampilkan hasil
                        displayDetectedFoods(detectedFoods)
                        showNutritionResults(nutritionInfo)
                    } else {
                        Toast.makeText(this@ResultActivity, "Nutrition data not available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorBody ?: "")
                    Toast.makeText(this@ResultActivity, "API Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ResultActivity, "Failed to contact server: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun saveScanResultToDatabase(foodName: String, nutritionInfo: List<NutritionInfo>) {
        val scanResult = ScanResult(
            foodName = foodName,
            nutritionInfo = nutritionInfo.joinToString { "${it.foodClass}: ${it.calories} kcal" },
            scanDate = System.currentTimeMillis()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val database = ScanResultDatabase.getDatabase(applicationContext)
            database.scanResultDao().insertScanResult(scanResult)
            Log.d("ResultActivity", "Scan result saved to database: $scanResult")
        }
    }


    private fun parseErrorMessage(errorBody: String): String {
        return try {
            val jsonObject = JSONObject(errorBody)
            jsonObject.getString("message")
        } catch (e: Exception) {
            Log.e("ResultActivity", "Failed to parse error message: $errorBody", e)
            "Unknown error"
        }
    }

    private fun displayDetectedFoods(detectedFoods: List<String>) {
        Toast.makeText(this, "Detected foods: ${detectedFoods.joinToString(", ")}", Toast.LENGTH_LONG).show()
    }

    private fun showNutritionResults(nutritionData: List<NutritionInfo>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNutrients)
        val totalCaloriesTextView = findViewById<TextView>(R.id.textResult)
        val cardViewResult = findViewById<CardView>(R.id.cardViewResult)

        cardViewResult.visibility = View.VISIBLE

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = NutritionResultAdapter(nutritionData)

        var totalCalories = 0f
        for (nutritionInfo in nutritionData) {
            totalCalories += nutritionInfo.calories
        }

        totalCaloriesTextView.text = "Total Kalori: ${String.format("%.1f", totalCalories)} kkal"

        val parentLayout = findViewById<View>(android.R.id.content)
        Snackbar.make(parentLayout, "This Food is Recommendation", Snackbar.LENGTH_LONG).show()

        Log.d("ResultActivity", "Total Calories: $totalCalories")
        Log.d("ResultActivity", "CardView Visibility: ${cardViewResult.visibility}")
    }

    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("ResultActivity", "Retrieved Auth Token: $token")
        return token
    }

    private fun navigateToLogin() {
        Log.d("ResultActivity", "Navigating to Login Activity.")
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}