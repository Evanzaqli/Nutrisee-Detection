package com.capstone.nutrisee.view

import DashboardResponse
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.capstone.nutrisee.R
import com.capstone.nutrisee.databinding.ActivityMainBinding
import com.capstone.nutrisee.login.OnboardingActivity
import com.capstone.nutrisee.service.ApiService
import com.capstone.nutrisee.utils.reduceFileImage
import com.capstone.nutrisee.utils.uriToFile
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private var currentPhotoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = getAuthToken()
        Log.d("MainActivity", "Token yang diterima: $token")

        if (token.isNullOrEmpty()) {
            navigateToLogin()
        } else {
            // Jika token ada, langsung panggil fetchDashboardData
            fetchDashboardData(token)
        }

        // Menangani WindowInsets (untuk edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupImageLaunchers()

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.button_camera -> {
                    showImageSourceDialog()
                    true
                }
                R.id.button_home -> {
                    true
                }
                R.id.button_setting -> {
                    val intent = Intent(this, SettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

    }
    private fun fetchDashboardData(token: String) {
        Log.d("MainActivity", "Memulai pemanggilan API dengan token: $token")

        // Tampilkan ProgressBar saat data sedang dimuat
        binding.progressBar.visibility = View.VISIBLE
        binding.textError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://node-service-dot-capstone-nutrisee-442807.et.r.appspot.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)

                val response = apiService.getDashboardData("Bearer $token")
                binding.progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val nutritionHistory = response.body()?.data?.nutritionHistory
                    if (nutritionHistory != null) {
                        Log.d("API Response", "Data berhasil diambil: $nutritionHistory")

                        // Update UI
                        with(binding) {0
                            textCarbs.text = "Carbohydrates: ${nutritionHistory.remainingCarbs} / ${nutritionHistory.targetCarbs}"
                            textProtein.text = "Protein: ${nutritionHistory.remainingProtein} / ${nutritionHistory.targetProtein}"
                            textFat.text = "Fat: ${nutritionHistory.remainingFat} / ${nutritionHistory.targetFat}"
                            textFiber.text = "Fiber: ${nutritionHistory.remainingFiber} / ${nutritionHistory.targetFiber}"
                            textCalories.text = "Calories: ${nutritionHistory.remainingCalories.toInt()} / ${nutritionHistory.targetCalories.toInt()} kcal"
                        }
                    } else {
                        Log.e("MainActivity", "Data tidak ditemukan.")
                        binding.textError.text = "Data tidak ditemukan."
                        binding.textError.visibility = View.VISIBLE
                    }
                } else {
                    Log.e("MainActivity", "Gagal mendapatkan data. Status: ${response.code()}")
                    binding.textError.text = "Gagal mendapatkan data. Status: ${response.code()}"
                    binding.textError.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Kesalahan jaringan: ${e.message}", e)
                binding.progressBar.visibility = View.GONE
                binding.textError.text = "Kesalahan jaringan: ${e.message}"
                binding.textError.visibility = View.VISIBLE
            }
        }
    }




    private fun navigateToLogin() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
    }

    private fun setupImageLaunchers() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleCameraResult(result)
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleGalleryResult(result)
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermission()
                    1 -> openGallery()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            currentPhotoFile = File.createTempFile("temp_image", ".jpg", externalCacheDir)

            val photoUri = currentPhotoFile?.let { file ->
                FileProvider.getUriForFile(this, "${packageName}.provider", file)
            }

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            cameraLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            Log.e("CameraError", "Error opening camera: ${e.message}", e)
            Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private fun handleCameraResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoFile?.let { file ->
                val reducedFile = file.reduceFileImage()
                navigateToResultActivity(Uri.fromFile(reducedFile))
            } ?: Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleGalleryResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                val file = uriToFile(uri, this).reduceFileImage()
                navigateToResultActivity(Uri.fromFile(file))
            } ?: Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gallery Canceled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToResultActivity(imageUri: Uri?) {
        if (imageUri != null) {
            val token = getAuthToken()
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("image_uri", imageUri.toString())
                putExtra("auth_token", token)
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    private fun saveAuthToken(token: String) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }

    private fun clearAuthToken() {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }
}