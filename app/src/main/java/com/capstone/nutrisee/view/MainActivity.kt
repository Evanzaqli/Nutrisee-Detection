package com.capstone.nutrisee.view

import NutritionHistory
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.capstone.nutrisee.R
import com.capstone.nutrisee.databinding.ActivityMainBinding
import com.capstone.nutrisee.login.OnboardingActivity
import com.capstone.nutrisee.utils.reduceFileImage
import com.capstone.nutrisee.utils.uriToFile
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: androidx.activity.result.ActivityResultLauncher<Intent>
    private var currentPhotoFile: File? = null
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = DashboardViewModelFactory(applicationContext)
        dashboardViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = getAuthToken()
        Log.d("MainActivity", "Token yang diterima: $token")

        if (token.isNullOrEmpty()) {
            navigateToLogin()
        } else {
            dashboardViewModel.nutritionHistory.observe(this, Observer { nutritionHistory ->
                if (nutritionHistory != null) {
                    updateUI(nutritionHistory)
                }
            })

            dashboardViewModel.fetchDashboardData(token)
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
                    replaceFragment(HistoryFragment())
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
    private var lastNutritionHistory: NutritionHistory? = null


    private fun updateUI(nutritionHistory: NutritionHistory) {
        with(binding) {
            textCarbs.text = "Carbohydrates: ${nutritionHistory.totalCarbs.toInt()} / ${nutritionHistory.targetCarbs.toInt()}"
            textProtein.text = "Protein: ${nutritionHistory.totalProtein.toInt()} / ${nutritionHistory.targetProtein.toInt()}"
            textFat.text = "Fat: ${nutritionHistory.totalFat.toInt()} / ${nutritionHistory.targetFat.toInt()}"
            textFiber.text = "Fiber: ${nutritionHistory.totalFiber.toInt()} / ${nutritionHistory.targetFiber.toInt()}"
            textCalories.text = "Calories: ${nutritionHistory.totalCalories.toInt()} / ${nutritionHistory.targetCalories.toInt()} kcal"
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

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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