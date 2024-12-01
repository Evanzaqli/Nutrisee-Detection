package com.capstone.nutrisee.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.nutrisee.R
import com.capstone.nutrisee.helper.ImageClassifierHelper
import com.capstone.nutrisee.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private lateinit var imageUri: Uri
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge UI
        setContentView(R.layout.activity_main)

        // Pengecekan token di SharedPreferences
        val token = getTokenFromSharedPreferences()

        setupImageLaunchers()

        // Menangani WindowInsets (untuk edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        findViewById<BottomNavigationView>(R.id.bottom_nav).setOnItemSelectedListener { item ->
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

    private fun getTokenFromSharedPreferences(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("token", null) // Mengambil token yang tersimpan
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java)) // Arahkan ke LoginActivity
        finish() // Menutup MainActivity sehingga pengguna tidak bisa kembali
    }

    private fun setupImageLaunchers() {
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage = result.data?.data
                openResultActivity(selectedImage)
            }
        }

        takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                openResultActivity(imageUri)
            }
        }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(this)
            .setTitle("Pilih sumber gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val photoFile = File.createTempFile("IMG_", ".jpg", externalCacheDir)
        imageUri = FileProvider.getUriForFile(this, "$packageName.provider", photoFile)
        takePhotoLauncher.launch(imageUri)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun openResultActivity(imageUri: Uri?) {
        if (imageUri != null) {
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("image_uri", imageUri.toString())
            }
            startActivity(intent)
        } else {
            Toast.makeText(this, "Gagal mengambil gambar", Toast.LENGTH_SHORT).show()
        }
    }
}
